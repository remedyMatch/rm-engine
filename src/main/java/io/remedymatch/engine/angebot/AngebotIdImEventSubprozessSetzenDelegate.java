package io.remedymatch.engine.angebot;

import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class AngebotIdImEventSubprozessSetzenDelegate implements JavaDelegate {

    public static final String VAR_ANFRAGE_ANGEBOT_ID = "anfrage_angebot_id";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariableLocal(VAR_ANFRAGE_ANGEBOT_ID, execution.getVariable(VAR_ANFRAGE_ANGEBOT_ID));
        val variable = execution.getVariable(VAR_ANFRAGE_ANGEBOT_ID);
        execution.removeVariable(VAR_ANFRAGE_ANGEBOT_ID);
        execution.setVariableLocal(VAR_ANFRAGE_ANGEBOT_ID, variable);

    }
}
