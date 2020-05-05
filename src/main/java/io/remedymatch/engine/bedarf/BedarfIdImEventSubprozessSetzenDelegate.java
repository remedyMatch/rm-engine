package io.remedymatch.engine.bedarf;

import lombok.val;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.springframework.stereotype.Component;

@Component
public class BedarfIdImEventSubprozessSetzenDelegate implements JavaDelegate {

    public static final String VAR_ANFRAGE_BEDARF_ID = "anfrage_bedarf_id";

    @Override
    public void execute(DelegateExecution execution) throws Exception {
        execution.setVariableLocal(VAR_ANFRAGE_BEDARF_ID, execution.getVariable(VAR_ANFRAGE_BEDARF_ID));
        val variable = execution.getVariable(VAR_ANFRAGE_BEDARF_ID);
        execution.removeVariable(VAR_ANFRAGE_BEDARF_ID);
        execution.setVariableLocal(VAR_ANFRAGE_BEDARF_ID, variable);

    }
}
