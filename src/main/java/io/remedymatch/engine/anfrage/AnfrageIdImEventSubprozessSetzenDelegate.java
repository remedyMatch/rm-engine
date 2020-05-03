package io.remedymatch.engine.anfrage;

import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class AnfrageIdImEventSubprozessSetzenDelegate implements JavaDelegate {

    public static final String VAR_ANFRAGE_ID = "angebot_anfrage_id";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariableLocal(VAR_ANFRAGE_ID, execution.getVariable(VAR_ANFRAGE_ID));
        val variable = execution.getVariable(VAR_ANFRAGE_ID);
        execution.removeVariable(VAR_ANFRAGE_ID);
        execution.setVariableLocal(VAR_ANFRAGE_ID, variable);

    }
}
