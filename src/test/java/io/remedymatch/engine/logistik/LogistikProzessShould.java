package io.remedymatch.engine.logistik;

import lombok.val;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import static io.remedymatch.engine.logistik.LogistikProzessConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
public class LogistikProzessShould extends AbstractProcessEngineRuleTest {


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
     * Der Spender erklärt sich bereit, die Ware zum Empfänger zu liefern.
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void lieferungDurchSpender() {
        val lieferprozess = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(lieferprozess).isActive();
        assertThat(lieferprozess).isWaitingAt(START_EVENT);

        // Start Event abschließen
        execute(job());

        // External Task - Lieferauftrag erstellen abschließen
        complete(externalTask(), withVariables("empfaenger", "testEmpfaenger", "spender", "testSpender"));

        // Nachricht senden -> Spender liefert Ware
        assertThat(lieferprozess).isWaitingFor(MESSAGE_LIEFERUNG, MESSAGE_ABHOLUNG);
        runtimeService().correlateMessage(MESSAGE_LIEFERUNG);
        assertThat(lieferprozess).isWaitingAt(TASK_LIEFERUNG_ERHALTEN);

        // Empfaenger bestaetigt Lieferung -> Ende des Prozesses
        complete(task());
        assertThat(lieferprozess).isEnded();
    }


    /**
     * Der Empfaenger erklärt sich bereit, die Ware vom Spender abzuholen
     */
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void abholungDurchEmpfaenger() {
        val lieferprozess = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(lieferprozess).isActive();
        assertThat(lieferprozess).isWaitingAt(START_EVENT);

        // Start Event abschließen
        execute(job());

        // External Task - Lieferauftrag erstellen abschließen
        complete(externalTask(), withVariables("empfaenger", "testEmpfaenger", "spender", "testSpender"));

        // Nachricht senden -> Empfaenger holt Ware ab
        assertThat(lieferprozess).isWaitingFor(MESSAGE_LIEFERUNG, MESSAGE_ABHOLUNG);
        runtimeService().correlateMessage(MESSAGE_ABHOLUNG);
        assertThat(lieferprozess).isWaitingAt(TASK_ABHOLUNG_BESTAETIGEN);

        // Empfaenger holt Ware ab -> Ende des Prozesses
        complete(task());
        assertThat(lieferprozess).isEnded();
    }

    /*
     * The goods are delivered by a service provider,
     * as the donor refuses to deliver them by himself /
     * the recipient cannot pick them up
     *//*
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void deliveryByServiceProvider_AsDonorAndRecipientRejectDelivery() {

    }
    */

    /**
     * The goods are delivered by a service provider,
     * because the recipient and the donor have not responded to the delivery-request.
     *//*
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void deliveryByServiceProvider_dueToResponseTimeout() {

    }
    */

    /**
     * A service provider has accepted a delivery request, but has no collected the goods.
     * The delivery-request is released again for all delivery-agents.
     *//*
    @Test
    @Deployment(resources = "bpmn/logisticProcess.bpmn")
    public void serviceProviderDoesNotPickupDelivery() {

    }
    */
}