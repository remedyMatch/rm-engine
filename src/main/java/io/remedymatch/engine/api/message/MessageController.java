package io.remedymatch.engine.api.message;

import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/remedy/message")
@Slf4j
public class MessageController {

    @PostMapping("/korrelieren")
    public ResponseEntity<Void> messageKorrelieren(final @RequestBody @Valid MessageKorrelierenRequest request) {

        boolean hasBusinessKey = StringUtils.isNotBlank(request.getBusinesskey());
        if (!hasBusinessKey && (request.getLocalVariablesEqual() == null || request.getLocalVariablesEqual().isEmpty())) {
            log.error("Mindestens eine der BusinessKey und VariablesEqual müssen gesetzt werden");
            return ResponseEntity.badRequest().build();
        }

        val runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

        MessageCorrelationBuilder correlationBuilder = runtimeService.createMessageCorrelation(request.getMessageKey());
        if (hasBusinessKey) {
            correlationBuilder.processInstanceBusinessKey(request.getBusinesskey());
        }
        if (request.getLocalVariablesEqual() != null && !request.getLocalVariablesEqual().isEmpty()) {
            correlationBuilder.localVariablesEqual(request.getLocalVariablesEqual());
        }
        if (request.getVariables() != null && !request.getVariables().isEmpty()) {
            correlationBuilder.setVariables(request.getVariables());
        }

        if (hasBusinessKey) {
            correlationBuilder.correlateExclusively();
        } else {
            correlationBuilder.correlateAll();
        }

        return ResponseEntity.ok().build();
    }
}
