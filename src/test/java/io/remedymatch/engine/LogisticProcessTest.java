package io.remedymatch.engine;

import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;

@SpringBootTest
public class LogisticProcessTest {

    @Rule
    public ProcessEngineRule rule;

    @Before
    public void setup() {
        rule = new ProcessEngineRule();
        init(rule.getProcessEngine());
    }

    /**
     * The donor agrees to deliver the goods to the recipient
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void deliveryByDonor() {

    }

    /**
     * The recipient agrees to pick up the goods from the donor
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void deliveryByRecipient() {

    }

    /**
     * The goods are delivered by a service provider,
     * as the donor refuses to deliver them by himself /
     * the recipient cannot pick them up
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void deliveryByServiceProvider_AsDonorAndRecipientRejectDelivery() {

    }

    /**
     * The goods are delivered by a service provider,
     * because the recipient and the donor have not responded to the delivery-request.
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void deliveryByServiceProvider_dueToResponseTimeout() {

    }

    /**
     * A service provider has accepted a delivery request, but has no collected the goods.
     * The delivery-request is released again for all delivery-agents.
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void serviceProviderDoesNotPickupDelivery() {

    }

}
