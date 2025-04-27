package org.flickit.assessment.kit.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record KitMetadata(

    @JsonProperty("goal") String goal,
    @JsonProperty("context") String context) {
}

