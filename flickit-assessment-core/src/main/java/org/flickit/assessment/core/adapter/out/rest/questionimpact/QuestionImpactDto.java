package org.flickit.assessment.core.adapter.out.rest.questionimpact;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.flickit.assessment.core.application.domain.QuestionImpact;

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
