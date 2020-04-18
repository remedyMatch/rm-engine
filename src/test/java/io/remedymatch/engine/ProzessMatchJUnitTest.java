package io.remedymatch.engine;


import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class ProzessMatchJUnitTest {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();


    @Before
    public void setup() {
        init(rule.getProcessEngine());
    }

    @Test
    @Deployment(resources = "bpmn/matchProzess.bpmn")
    public void testHappyPath() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("match_prozess", withVariables("empfaenger", "Camunda"));

        assertThat(processInstance).isWaitingAt("match_prozess_annehmen").
                externalTask().hasTopicName("matchErstellen");
        complete(externalTask());

        assertThat(processInstance).isWaitingAt("match_prozess_empfang_bestaetigen");
        assertThat(task()).hasCandidateGroup("Camunda");
        complete(task());

        assertThat(processInstance).isWaitingAt("match_prozess_wareneingang_verarbeiten").
                externalTask().hasTopicName("auslieferungBestaetigung");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("match_prozess_auslieferung_bestaetigt");
    }


    @Test
    @Deployment(resources = "bpmn/matchProzess.bpmn")
    public void testStornierungsNachricht() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("empfaenger", "Camunda");

        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("match_prozess")
                .setVariables(variables)
                .startAfterActivity("match_prozess_annehmen")
                .execute();
        assertThat(processInstance).isWaitingAt("match_prozess_empfang_bestaetigen");

        runtimeService().createMessageCorrelation("stornierungErhalten")
                .setVariable("reason", "Mag nicht mehr.")
                .processInstanceVariableEquals("empfaenger", "Camunda")
                .correlateWithResult().getProcessInstance();


        assertThat(processInstance).isEnded().hasPassed("EndEvent_077de68");

    }


}
