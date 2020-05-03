package io.remedymatch.engine;

import io.remedymatch.engine.benachrichtigung.BenachrichtigungService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;


@SpringBootTest
public class ProzessAngebotAnfrageJUnitTest {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Autowired
    private BenachrichtigungService benachrichtigungService;

    @Before
    public void setUp() {
        init(rule.getProcessEngine());

        Mocks.register("benachrichtigungService", benachrichtigungService);
    }

    //	@Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testHappyPath() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess",
                withVariables("institution", "Camunda"));

        assertThat(processInstance).isWaitingAt("StartEvent_1");
        execute(job());

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_beantworten");
        assertThat(task()).hasCandidateGroup("Camunda");
        complete(task(), withVariables("angenommen", true));

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_match_prozess_starten").externalTask()
                .hasTopicName("angebotMatchProzessStarten");
        complete(externalTask(), withVariables("angebotmenge", 3));

        assertThat(processInstance).isEnded().hasPassed("MatchProcessStartedEndEvent");
    }

    //@Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testHappyPathMitAufgebrauchterAngebotsMenge() {
        ProcessInstance processInstance = runtimeService().createProcessInstanceByKey("angebot_anfrage_prozess")
                .setVariable("angebotmenge", 0).startAfterActivity("angebot_anfrage_prozess_match_prozess_starten")
                .execute();

        assertThat(processInstance).isWaitingAt("AnfragenSuchenUndLoeschenTask").externalTask()
                .hasTopicName("AnfragenSuchenUndLoeschen");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("MatchProcessStartedEndEvent");
    }

    @Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testRejectOffer() {

        ProcessInstance processInstance = runtimeService().createProcessInstanceByKey("angebot_anfrage_prozess")
                .setVariable("angenommen", false).startAfterActivity("angebot_anfrage_prozess_beantworten").execute();

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_anfrage_ablehnen").externalTask()
                .hasTopicName("angebot_anfrage_ablehnen_topic");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("angebot_anfrage_prozess_EndEvent_AnfrageAbgelehnt");

    }

    @Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testCancleOffer() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess",
                withVariables("institution", "Camunda"));

        runtimeService().createMessageCorrelation("angebot_anfrage_stornieren").setVariable("reason", "Mag nicht mehr.")
                .processInstanceVariableEquals("institution", "Camunda").correlateWithResult().getProcessInstance();

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_anfrage_stornieren").externalTask()
                .hasTopicName("angebot_anfrage_stornieren_topic");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("angebot_anfrage_prozess_EndEvent_AnfrageStorniert");

    }

}
