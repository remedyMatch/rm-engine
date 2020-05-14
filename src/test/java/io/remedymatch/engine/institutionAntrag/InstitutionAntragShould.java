package io.remedymatch.engine.institutionAntrag;

import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.test.Deployment;
import org.camunda.bpm.spring.boot.starter.test.helper.AbstractProcessEngineRuleTest;
import org.junit.Test;

import static io.remedymatch.engine.institutionAntrag.InstitutionAntragProzessConstants.*;
import static org.camunda.bpm.engine.test.assertions.bpmn.BpmnAwareTests.*;

public class InstitutionAntragShould extends AbstractProcessEngineRuleTest {

    @Test
    @Deployment(resources = "bpmn/institutionAntragProzess.bpmn")
    public void antrag_annehmen_und_abschließen() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(processInstance).isStarted();
        execute(job());

        assertThat(processInstance).isWaitingAt(TASK_ANTRAG_PRUEFEN);
        complete(task(), withVariables(VAR_ANTRAG_ANNEHMEN, true));

        assertThat(processInstance).isWaitingAt(SERVICE_ANTRAG_GENEHMIGEN).
                externalTask().hasTopicName(TOPIC_ANTRAG_GENEHMIGEN);
        complete(externalTask());

        assertThat(processInstance).isWaitingAt(SERVICE_INST_ANLEGEN).
                externalTask().hasTopicName(TOPIC_INST_ANLEGEN);
        complete(externalTask());

        assertThat(processInstance).isWaitingAt(SERVICE_INST_ZUWEISEN).
                externalTask().hasTopicName(TOPIC_INST_ZUWEISEN);
        complete(externalTask());

        assertThat(processInstance).isEnded();
    }

    @Test
    @Deployment(resources = "bpmn/institutionAntragProzess.bpmn")
    public void antrag_ablehnen() {
        ProcessInstance processInstance = runtimeService().startProcessInstanceByKey(PROZESS_KEY);
        assertThat(processInstance).isStarted();

        execute(job());

        assertThat(processInstance).isWaitingAt(TASK_ANTRAG_PRUEFEN);
        complete(task(), withVariables(VAR_ANTRAG_ANNEHMEN, false));

        assertThat(processInstance).isWaitingAt(SERVICE_ANTRAG_ABLEHNEN).
                externalTask().hasTopicName(TOPIC_ANTRAG_ABLEHNEN);
        complete(externalTask());

        assertThat(processInstance).isEnded();
    }

}
