package org.flickit.flickitassessmentcore.adapter.out.rest.questionImpact;

import org.flickit.flickitassessmentcore.domain.QuestionImpact;

public class QuestionImpactMapper {

    public static LoadQuestionImpactPort.Result toResult(List<QuestionImpactRestAdapter.QuestionImpactDto> items) {
        return new LoadQuestionImpactPort.Result(
            toDomainModel(items.get(0))
        );
    }

    private static QuestionImpact toDomainModel(QuestionImpactRestAdapter.QuestionImpactDto questionImpactDto) {
        return new QuestionImpact(
            dto.id(),
            dto.maturityLevel(),
            dto.qualityAttribute(),
            dto.weight()
        );
    }
}
