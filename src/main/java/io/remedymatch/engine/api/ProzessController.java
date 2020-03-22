package io.remedymatch.engine.api;

import lombok.val;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/restapi/prozess")
public class ProzessController {

    @PostMapping("/start")
    public ResponseEntity<String> liefereTasksFuerInstitution(@RequestBody ProzessStartRequest request) {
        val variables = Variables.createVariables();
        variables.putValue("institution", request.getInstitutionId());
        val prozessInstanz = ProcessEngines.getDefaultProcessEngine().getRuntimeService().startProcessInstanceByKey(request.getProzessKey(), request.getAnfrageId(), variables);
        return ResponseEntity.ok(prozessInstanz.getProcessDefinitionId());
    }
}
