package io.remedymatch.engine.api.message;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.val;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/remedy/message")
@Slf4j
public class MessageController {

	@PostMapping("/korrelieren")
	public ResponseEntity<Void> messageKorrelieren(final @RequestBody @Valid MessageKorrelierenRequest request) {

		boolean hasProzessInstanzId = StringUtils.isNotBlank(request.getProzessInstanzId());
		if (!hasProzessInstanzId && (request.getVariablesEqual() == null || request.getVariablesEqual().isEmpty())) {
			log.error("Mindestens eine der ProzessInstanzId und VariablesEqual muessen gesetzt werden");
			return ResponseEntity.badRequest().build();
		}

		val runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();

		MessageCorrelationBuilder correlationBuilder = runtimeService.createMessageCorrelation(request.getMessageKey());
		if (hasProzessInstanzId) {
			correlationBuilder.processInstanceId(request.getProzessInstanzId());
		}
		if (request.getVariablesEqual() != null && !request.getVariablesEqual().isEmpty()) {
			correlationBuilder.processInstanceVariablesEqual(request.getVariablesEqual());
		}
		if (request.getVariables() != null && !request.getVariables().isEmpty()) {
			correlationBuilder.setVariables(request.getVariables());
		}

		if (hasProzessInstanzId) {
			correlationBuilder.correlateExclusively();
		} else {
			correlationBuilder.correlateAll();
		}

		return ResponseEntity.ok().build();
	}
}
