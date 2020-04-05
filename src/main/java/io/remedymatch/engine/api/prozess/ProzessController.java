package io.remedymatch.engine.api.prozess;

import lombok.val;
import org.camunda.bpm.engine.ProcessEngines;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/restapi/prozess")
public class ProzessController {


    @PostMapping("/start")
    public ResponseEntity<String> prozessStarten(@RequestBody @Valid ProzessStartRequest request) {


        val prozessInstanz = ProcessEngines.getDefaultProcessEngine()
                .getRuntimeService()
                .startProcessInstanceByKey(request.getProzessKey(), request.getBusinessKey(), request.getVariables());
        return ResponseEntity.ok(prozessInstanz.getProcessInstanceId());
    }
}
