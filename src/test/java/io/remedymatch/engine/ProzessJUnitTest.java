package io.remedymatch.engine;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.ProcessEngineTests.*;


public class ProzessJUnitTest {
    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();


    @Before
    public void setup() {
        init(rule.getProcessEngine());
    }

    @Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testHappyPath() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess", withVariables("institution", "Camunda"));

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_beantworten");
        assertThat(task()).hasCandidateGroup("Camunda");
        complete(task(), withVariables("angenommen", true));

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_match_prozess_starten").
                externalTask().hasTopicName("angebotMatchProzessStarten");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("MatchProzessGestartetEndEvent");


    }


    @Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testAnfrageAbegelehnt() {
        Map<String, Object> abgelehntMap = new HashMap<String, Object>();
        abgelehntMap.put("angenommen", false);

        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("angebot_anfrage_prozess")
                .setVariables(abgelehntMap)
                .startAfterActivity("angebot_anfrage_prozess_beantworten")
                .execute();

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_stornierung_verarbeiten").
                externalTask().hasTopicName("angebotAnfrageAblehnen");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("AngebotStorniertEnde");


    }
}
