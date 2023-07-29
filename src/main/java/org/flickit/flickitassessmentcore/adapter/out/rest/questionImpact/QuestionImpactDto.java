package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import com.fasterxml.jackson.annotation.JsonProperty;

public record QuestionImpactDto(Long id,
                         Integer level,
                         Double weight,
                         @JsonProperty("maturity_level")
                         Long maturityLevel,
                         Long metric,
                         @JsonProperty("quality_attribute")
                         Long qualityAttribute) {
}
