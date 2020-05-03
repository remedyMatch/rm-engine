package io.remedymatch.engine.angebot;

import io.remedymatch.engine.anfrage.AnfrageIdImEventSubprozessSetzenDelegate;
import io.remedymatch.engine.anfrage.AnfrageIdLokalSetzenDelegate;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.engine.test.mock.Mocks;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static io.remedymatch.engine.angebot.AngebotProzessConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

@SpringBootTest
public class AngebotProzessShould extends AbstractProcessEngineRuleTest {

    @Before
    public void setUp() {
        Mocks.register("anfrageIdLokalSetzenDelegate", new AnfrageIdLokalSetzenDelegate());
        Mocks.register("anfrageIdImEventSubprozessSetzenDelegate", new AnfrageIdImEventSubprozessSetzenDelegate());
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void angebot_stornieren() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);
        assertThat(processInstance).isWaitingFor(MESSAGE_ANGEBOT_GESCHLOSSEN);

        runtimeService().correlateMessage(MESSAGE_ANGEBOT_GESCHLOSSEN);

        assertThat(processInstance).isWaitingAt(TASK_ANGEBOT_SCHLIESSEN_STORNIERUNG)
                .externalTask().hasTopicName(TOPIC_ANGEBOT_SCHLIESSEN);

        complete(externalTask());

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void angebot_anzahl_erreicht() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);

        runtimeService().setVariable(processInstance.getId(), VAR_ANZAHL, BigDecimal.valueOf(0));
        assertThat(processInstance).isWaitingAt(TASK_ANGEBOT_SCHLIESSEN_ANZAHL)
                .externalTask().hasTopicName(TOPIC_ANGEBOT_SCHLIESSEN);

        complete(externalTask());
        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void angebot_anfrage_erhalten_und_angenommen() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY, BUSINESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);
        assertThat(processInstance).isWaitingFor(MESSAGE_ANFRAGE_ERHALTEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "a23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);
        runtimeService().createMessageCorrelation(MESSAGE_ANFRAGE_BEARBEITET).localVariableEquals(VAR_ANFRAGE_ID, "a23423432").setVariable(VAR_ANFRAGE_ANGENOMMEN, true).correlate();

        assertThat(processInstance).isWaitingAt(TASK_MATCH_ANLEGEN)
                .externalTask().hasTopicName(TOPIC_MATCH_ANLEGEN);
        complete(externalTask());

        assertThat(processInstance).isWaitingAt(TASK_LOGISTIKAUFTRAG_ERSTELLEN)
                .externalTask().hasTopicName(TOPIC_LOGISTIKAUFTRAG_ERSTELLEN);
        complete(externalTask());

        assertThat(processInstance).hasPassed(END_LOGISTIKAUFTRAG_ERSTELLT);
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void angebot_anfrage_erhalten_und_abgelehnt() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY, BUSINESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);
        assertThat(processInstance).isWaitingFor(MESSAGE_ANFRAGE_ERHALTEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "a23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);
        runtimeService().createMessageCorrelation(MESSAGE_ANFRAGE_BEARBEITET).localVariableEquals(VAR_ANFRAGE_ID, "a23423432").setVariable(VAR_ANFRAGE_ANGENOMMEN, false).correlate();

        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_ABLEHNEN)
                .externalTask().hasTopicName(TOPIC_ANFRAGE_ABLEHNEN);
        complete(externalTask());

        assertThat(processInstance).hasPassed(END_ANFRAGE_ABGELEHNT);
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void angebot_aktualitaet_benachrichtigen() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);

        execute(jobQuery().timers().singleResult());

        assertThat(processInstance).isWaitingAt(TASK_BENACHRICHTUNG_EINSTELLEN)
                .externalTask().hasTopicName(TOPIC_BENACHRICHTUNG_EINSTELLEN);
        complete(externalTask());

        assertThat(processInstance).hasPassed(END_BENACHRICHTIGUNG_EINGESTELLT);
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void angebot_rest_veraendern_und_beenden() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY, BUSINESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN)
                .isWaitingFor(MESSAGE_REST_ANGEBOT_GEAENDERT);

        runtimeService().correlateMessage(MESSAGE_REST_ANGEBOT_GEAENDERT, BUSINESS_KEY, withVariables(VAR_ANZAHL, 0));

        assertThat(processInstance).isWaitingAt(TASK_ANGEBOT_SCHLIESSEN_ANZAHL)
                .hasPassed(END_REST_ANGEBOT_GEAENDERT)
                .externalTask().hasTopicName(TOPIC_ANGEBOT_SCHLIESSEN);

        complete(externalTask());
        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void mehrere_angebot_anfrage_erhalten() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY, BUSINESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);
        assertThat(processInstance).isWaitingFor(MESSAGE_ANFRAGE_ERHALTEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "a23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN).isWaitingFor(MESSAGE_ANFRAGE_BEARBEITET);
        runtimeService().createMessageCorrelation(MESSAGE_ANFRAGE_BEARBEITET).localVariableEquals(VAR_ANFRAGE_ID, "a23423432").setVariable(VAR_ANFRAGE_ANGENOMMEN, true).correlate();

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "b23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "c23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);

        //
//        val task1 = taskQuery().taskCandidateUser("b23423432").singleResult();
//        val task2 = taskQuery().taskCandidateUser("c23423432").singleResult();
//
//        val ex1 = executionQuery().variableValueEquals(VAR_ANFRAGE_ID, "b23423432").singleResult();
//        val ex2 = executionQuery().variableValueEquals(VAR_ANFRAGE_ID, "a23423432").singleResult();

    }

    @Test
    @Deployment(resources = "bpmn/angebotProzess.bpmn")
    public void mehrere_angebot_anfrage_erhalten_und_zwei_stornieren() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY, BUSINESS_KEY);
        assertThat(processInstance).isStarted();

        execute(jobQuery().activityId(START_ANGEBOT_ERHALTEN).singleResult());

        assertThat(processInstance).isWaitingAt(TASK_STORNIERUNG_ERHALTEN);
        assertThat(processInstance).isWaitingFor(MESSAGE_ANFRAGE_ERHALTEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "a23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "b23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);

        runtimeService().correlateMessage(MESSAGE_ANFRAGE_ERHALTEN, BUSINESS_KEY, withVariables(VAR_ANFRAGE_ID, "c23423432"));
        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_BEARBEITEN);


        assertThat(processInstance).isWaitingFor(MESSAGE_ANFRAGE_STORNIEREN);
        runtimeService().createMessageCorrelation(MESSAGE_ANFRAGE_STORNIEREN).localVariableEquals(VAR_ANFRAGE_ID, "a23423432").correlate();

        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_STORNIEREN)
                .externalTask().hasTopicName(TOPIC_ANFRAGE_STORNIEREN);
        complete(externalTask());

        assertThat(processInstance).isWaitingFor(MESSAGE_ANFRAGE_STORNIEREN);
        runtimeService().createMessageCorrelation(MESSAGE_ANFRAGE_STORNIEREN).localVariableEquals(VAR_ANFRAGE_ID, "b23423432").correlate();

        assertThat(processInstance).isWaitingAt(TASK_ANFRAGE_STORNIEREN)
                .externalTask().hasTopicName(TOPIC_ANFRAGE_STORNIEREN);
        complete(externalTask());


        assertThat(processInstance).hasPassed(END_ANFRAGE_STORNIERT);
    }
}


