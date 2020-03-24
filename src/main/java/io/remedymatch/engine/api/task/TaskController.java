package io.remedymatch.engine.api.task;

import lombok.val;
import org.camunda.bpm.engine.ProcessEngines;
import org.camunda.bpm.engine.task.Task;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/restapi/task")
public class TaskController {

    @GetMapping("/institution/{institutionId}")
    public ResponseEntity<List<TaskDTO>> liefereTasksFuerInstitution(@PathVariable("institutionId") String institutionId) {
        val tasks = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().taskCandidateGroup(institutionId).list();
        if (tasks == null) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.ok(
                tasks.stream()
                        .map(task -> mapToDTO(
                                task,
                                institutionId,
                                task.getTaskDefinitionKey(),
                                ProcessEngines.getDefaultProcessEngine()
                                        .getRuntimeService()
                                        .getVariable(task.getProcessInstanceId(), "anfrageId").toString())).
                        collect(Collectors.toList()));
    }

    @PostMapping("/{taskId}")
    public ResponseEntity<Void> taskAbschliessen(@PathVariable("taskId") String taskId, @RequestBody TaskAbschliessenRequest request) {
        ProcessEngines.getDefaultProcessEngine().getTaskService().complete(taskId, request.getVariables());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{institutionId}/{taskId}")
    public ResponseEntity<TaskDTO> taskLaden(@PathVariable("taskId") String taskId, @PathVariable("institutionId") String institutionId) {
        val task = ProcessEngines.getDefaultProcessEngine().getTaskService().createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return ResponseEntity.ok().build();
        }

        val anfrageId = ProcessEngines.getDefaultProcessEngine().getRuntimeService().getVariable(task.getProcessInstanceId(), "anfrageId").toString();

        return ResponseEntity.ok(mapToDTO(task, task.getTaskDefinitionKey(), institutionId, anfrageId));
    }

    private TaskDTO mapToDTO(Task task, String taskKey, String institutionId, String anfrageId) {
        return TaskDTO.builder().institution(institutionId).prozessInstanceId(task.getProcessInstanceId()).taskId(task.getId()).objektId(anfrageId).taskKey(taskKey).build();
    }

}
