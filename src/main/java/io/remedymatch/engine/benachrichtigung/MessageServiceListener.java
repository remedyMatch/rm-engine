package io.remedymatch.engine.benachrichtigung;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;

import javax.inject.Named;

@Named("MessageService")
public class MessageServiceListener implements TaskListener {

    private BenachrichtigungService messageService = new BenachrichtigungService();

    @Override
    public void notify(DelegateTask delegateTask) {
        System.out.println("Been is run! Hi Nele");
        messageService.benachrichtigungSenden(delegateTask.getExecution());
    }
}
