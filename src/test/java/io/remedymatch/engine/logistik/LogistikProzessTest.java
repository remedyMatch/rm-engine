package io.remedymatch.engine.logistik;

import lombok.val;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.ProcessEngineRule;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.remedymatch.engine.logistik.LogistikProzessConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.AbstractAssertions.init;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
public class LogistikProzessTest {

    @Rule
    public ProcessEngineRule rule = new ProcessEngineRule();

    @Before
    public void setup() {
        rule = new ProcessEngineRule();
        init(rule.getProcessEngine());
    }

    /**
     * Der Prozess ist gestartet
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void prozessIstGestartet() {
        val instanz = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(instanz).isActive();
    }

    /**
     * The recipient agrees to pick up the goods from the donor
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void lieferungDurchSpender() {
        val instanz = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(instanz).isActive();
        assertThat(instanz).isWaitingAt(START_EVENT);
        //Start Event abschließen
        execute(job());

        //Lieferauftrag erstellen abschließen
        complete(externalTask(), withVariables("empfaenger", "testEmpfaenger", "spender", "testSpender"));

        assertThat(instanz).isWaitingFor(MESSAGE_LIEFERUNG, MESSAGE_ABHOLUNG);
        runtimeService().correlateMessage(MESSAGE_LIEFERUNG);
        assertThat(instanz).isWaitingAt(TASK_LIEFERUNG_ERHALTEN);
        complete(task());
        assertThat(instanz).isEnded();

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
