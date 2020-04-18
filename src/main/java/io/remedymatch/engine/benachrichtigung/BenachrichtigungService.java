package io.remedymatch.engine.benachrichtigung;

import javax.inject.Named;

import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Named
public class BenachrichtigungService {

	static final String BENACHRICHTIGUNG_PROCESS_KEY = "rm_benachrichtigung";

	static final String ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_NAME = "benachrichtigungAnName";
	static final String ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_EMAIL = "benachrichtigungAnEmail";
	static final String ENGINE_VARIABLE_BENACHRICHTIGUNG_KEY = "benachrichtigungKey";

	@Autowired
	private RuntimeService runtimeService;

	public void benachrichtigungSenden(final DelegateExecution execution, final String benachrichtigungKey) {
		runtimeService.startProcessInstanceByKey( //
				BENACHRICHTIGUNG_PROCESS_KEY, //
				execution.getBusinessKey(), //
				Variables.createVariables() //
						.putValue(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_NAME,
								execution.getVariable(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_NAME)) //
						.putValue(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_EMAIL,
								execution.getVariable(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_EMAIL)) //
						.putValue(ENGINE_VARIABLE_BENACHRICHTIGUNG_KEY, benachrichtigungKey));
	}

	public void benachrichtigungSenden(final DelegateExecution execution) {
		runtimeService.startProcessInstanceByKey( //
				BENACHRICHTIGUNG_PROCESS_KEY, //
				execution.getBusinessKey(), //
				Variables.createVariables() //
						.putValue(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_NAME,
								execution.getVariable(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_NAME)) //
						.putValue(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_EMAIL,
								execution.getVariable(ENGINE_VARIABLE_BENACHRICHTIGUNG_AN_EMAIL)) //
						.putValue(ENGINE_VARIABLE_BENACHRICHTIGUNG_KEY,
								execution.getVariable(ENGINE_VARIABLE_BENACHRICHTIGUNG_KEY)));
	}
}
