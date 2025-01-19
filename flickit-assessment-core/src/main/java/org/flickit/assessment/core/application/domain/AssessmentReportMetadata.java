package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public record AssessmentReportMetadata(
    @JsonProperty("intro") String intro,
    @JsonProperty("prosAndCons") String prosAndCons,
    @JsonProperty("steps") String steps,
    @JsonProperty("participants") String participants) {
}
