package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import org.flickit.flickitassessmentcore.domain.QuestionImpact;

public class QuestionImpactMapper {

    public static QuestionImpact toDomainModel(QuestionImpactDto dto) {
        return new QuestionImpact(
            dto.id(),
            dto.maturityLevel(),
            dto.qualityAttribute(),
            dto.weight()
        );
    }
}
