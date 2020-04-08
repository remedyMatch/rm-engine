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
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey("donation-offer-process", withVariables("institution", "Camunda"));

        assertThat(processInstance).isWaitingAt("AnswerDonationOfferTask");
        assertThat(task()).hasCandidateGroup("Camunda");
        complete(task(), withVariables("approved", true));

        assertThat(processInstance).isWaitingAt("StartMatchProcessTask").
                externalTask().hasTopicName("StartMatchProcess");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("MatchProcessStartedEndEvent");


    }


    @Test
    @Deployment(resources = "bpmn/angebotAnfrageProzess.bpmn")
    public void testRejectOffer() {
        Map<String, Object> notApprovedMap = new HashMap<String, Object>();
        notApprovedMap.put("approved", false);

        ProcessInstance processInstance = runtimeService()
                .createProcessInstanceByKey("donation-offer-process")
                .setVariables(notApprovedMap)
                .startAfterActivity("AnswerDonationOfferTask")
                .execute();

        assertThat(processInstance).isWaitingAt("CancleDonationOfferTask1").
                externalTask().hasTopicName("CancleDonationOffer");
        complete(externalTask());

        assertThat(processInstance).isEnded().hasPassed("DonationOfferCancledEndEvent");


    }
}
