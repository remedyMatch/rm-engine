package io.remedymatch.engine.backend;


import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.HashMap;
import java.util.Map;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
public class ProzessMatchJUnitTest {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    private static final String PROCESS_KEY = "match_prozess";

    private static final String ACTIVITY_MATCH_ERSTELLEN = "match_prozess_annehmen";
    private static final String TOPIC_MATCH_ERSTELLEN = "matchErstellen";
    private static final String ACTIVITY_WARENEINGANG_BESTAETIGEN = "match_prozess_empfang_bestaetigen";
    private static final String ACTIVITY_WARENEINGANG_VERARBEITEN = "match_prozess_wareneingang_verarbeiten";
    private static final String TOPIC_WARENEINGANG_VERARBEITEN = "auslieferungBestaetigung";
    private static final String EVENT_AUSLIEFERUNG_BESTAETIGT = "match_prozess_auslieferung_bestaetigt";
    private static final String STORNIERUNG_ERHALTEN = "stornierungErhalten";


    @Before
    public void setup() {
        init(rule.getProcessEngine());
    }

    @Test
    @Deployment(resources = "bpmn/matchProzess.bpmn")
    public void testHappyPath() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROCESS_KEY, withVariables("empfaenger", "Camunda"));

        assertThat(processInstance).isWaitingAt(ACTIVITY_MATCH_ERSTELLEN).
                externalTask().hasTopicName(TOPIC_MATCH_ERSTELLEN);
        complete(externalTask());

        assertThat(processInstance).isWaitingAt(ACTIVITY_WARENEINGANG_BESTAETIGEN);
        assertThat(task()).hasCandidateGroup("Camunda");
        complete(task());

        assertThat(processInstance).isWaitingAt(ACTIVITY_WARENEINGANG_VERARBEITEN).
                externalTask().hasTopicName(TOPIC_WARENEINGANG_VERARBEITEN);
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed(EVENT_AUSLIEFERUNG_BESTAETIGT);
    }


    @Test
    @Deployment(resources = "bpmn/matchProzess.bpmn")
    public void testStornierungVerarbeiten() {
        Map<String, Object> variables = new HashMap<String, Object>();
        variables.put("empfaenger", "Camunda");

        ProcessInstance processInstance = runtimeService().createProcessInstanceByKey(PROCESS_KEY)
                .setVariables(variables)
                .startAfterActivity(ACTIVITY_MATCH_ERSTELLEN)
                .execute();
        assertThat(processInstance).isWaitingAt(ACTIVITY_WARENEINGANG_BESTAETIGEN);

        runtimeService().createMessageCorrelation(STORNIERUNG_ERHALTEN)
                .setVariable("reason", "Mag nicht mehr.")
                .processInstanceVariableEquals("empfaenger", "Camunda")
                .correlateWithResult().getProcessInstance();

        assertThat(processInstance).isEnded().hasPassed("EndEvent_077de68");
    }


}
