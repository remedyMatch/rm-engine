package io.remedymatch.engine;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

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
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("angebot_anfrage_prozess");

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_beantworten");
        complete(task(), withVariables("angenommen", true));

        assertThat(processInstance).isWaitingAt("angebot_anfrage_prozess_match_prozess_starten").
                externalTask().hasTopicName("angebotMatchProzessStarten");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("MatchProzessGestartetEndEvent");


    }
}
