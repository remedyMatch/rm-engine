package io.remedymatch.engine.api;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TaskDTO {

    public String taskId;
    public String processInstanceId;
    public String institution;

}
