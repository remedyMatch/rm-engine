package io.remedymatch.engine.api.message;

import lombok.val;
import org.camunda.bpm.engine.ProcessEngines;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/restapi/message")
public class MessageController {

    @PostMapping("/korrelieren")
    public ResponseEntity<Void> messageKorrelieren(@RequestBody @Valid MessageKorrelierenRequest request) {
        val runtimeService = ProcessEngines.getDefaultProcessEngine().getRuntimeService();
        runtimeService.createMessageCorrelation(request.getMessageKey())
                .processInstanceId(request.getProzessInstanzId())
                .setVariables(request.getVariables())
                .correlate();
        return ResponseEntity.ok().build();
    }
}
