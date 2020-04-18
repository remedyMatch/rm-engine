package io.remedymatch.engine;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class ProzessBedarfAnfrageJUnitTest {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();


    @Before
    public void setup() {
        init(rule.getProcessEngine());
    }

    @Test
    @Deployment(resources = "bpmn/bedarfAnfrageProzess.bpmn")
    public void testHappyPath() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("bedarf_anfrage_prozess", withVariables("institution", "Camunda"));

        assertThat(processInstance).isWaitingAt("StartEvent_1");
        execute(job());

        assertThat(processInstance).isWaitingAt("bedarf_anfrage_prozess_beantworten");
        assertThat(task()).hasCandidateGroup("Camunda");
        complete(task(), withVariables("angenommen", true));

        assertThat(processInstance).isWaitingAt("bedarf_anfrage_prozess_match_prozess_starten").
                externalTask().hasTopicName("bedarfMatchProzessStarten");


        complete(externalTask(), withVariables("bedarfsmenge", 3));

        assertThat(processInstance).isEnded().hasPassed("EndEvent_1m3wuzk");
    }

    @Test
    @Deployment(resources = "bpmn/bedarfAnfrageProzess.bpmn")
    public void testHappyPathMitGedecktemBedarf() {

        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("bedarf_anfrage_prozess")
                .setVariable("bedarfsmenge", 0)
                .startAfterActivity("bedarf_anfrage_prozess_match_prozess_starten")
                .execute();

        assertThat(processInstance).isWaitingAt("AnfrageProzesseSuchenUndBeendenTask").
                externalTask().hasTopicName("AnfrageProzesseSuchenundBeenden");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("EndEvent_1m3wuzk");
    }


    @Test
    @Deployment(resources = "bpmn/bedarfAnfrageProzess.bpmn")
    public void testRejectOffer() {

        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("bedarf_anfrage_prozess")
                .setVariable("angenommen", false)
                .startAfterActivity("bedarf_anfrage_prozess_beantworten")
                .execute();

        assertThat(processInstance).isWaitingAt("bedarf_anfrage_prozess_stornierung_verarbeiten").
                externalTask().hasTopicName("bedarfAnfrageAblehnen");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("EndEvent_0txu0vj");


    }

    @Test
    @Deployment(resources = "bpmn/bedarfAnfrageProzess.bpmn")
    public void testStornierungsNachricht() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("bedarf_anfrage_prozess", withVariables("institution", "Camunda"));

        runtimeService().createMessageCorrelation("bedarf_anfrage_stornieren")
                .setVariable("reason", "Mag nicht mehr.")
                .processInstanceVariableEquals("institution", "Camunda")
                .correlateWithResult().getProcessInstance();

        assertThat(processInstance).isWaitingAt("StartEvent_0xfsbi5");
        execute(job());

        assertThat(processInstance).isWaitingAt("Task_1qi5jni").
                externalTask().hasTopicName("bedarfAnfrageAblehnen");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("EndEvent_0zwid5r");


    }
}
