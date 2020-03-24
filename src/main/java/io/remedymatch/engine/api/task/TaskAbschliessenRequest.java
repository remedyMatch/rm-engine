package io.remedymatch.engine.api.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TaskAbschliessenRequest {

    private Map<String, Object> variables;

}
