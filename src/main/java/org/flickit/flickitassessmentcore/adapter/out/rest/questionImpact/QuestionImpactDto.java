package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.flickitassessmentcore.domain.QuestionImpact;

public record QuestionImpactDto(Long id,
                                Integer weight,
                                @JsonProperty("maturity_level_id")
                                Long maturityLevelId,
                                @JsonProperty("quality_attribute_id")
                                Long qualityAttributeId) {

    public QuestionImpact dtoToDomain() {
        return new QuestionImpact(id, weight, maturityLevelId);
    }
}
