package org.flickit.assessment.kit.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Size;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record KitMetadata(
    @Size(min = 3, message = KIT_METADATA_GOAL_SIZE_MIN)
    @Size(max = 300, message = KIT_METADATA_GOAL_SIZE_MAX)
    @JsonProperty("goal") String goal,

    @Size(min = 3, message = KIT_METADATA_CONTEXT_SIZE_MIN)
    @Size(max = 300, message = KIT_METADATA_CONTEXT_SIZE_MAX)
    @JsonProperty("context") String context) {
}

