package io.remedymatch.engine.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProzessStartRequest {

    @NotNull
    private String prozessKey;

    @NotNull
    private String anfrageId;

    @NotNull
    private String institutionId;

}
