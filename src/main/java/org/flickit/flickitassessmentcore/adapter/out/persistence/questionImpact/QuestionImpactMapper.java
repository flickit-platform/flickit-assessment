package org.flickit.flickitassessmentcore.adapter.out.persistence.questionImpact;

import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.domain.QuestionImpact;

import java.util.List;

public class QuestionImpactMapper {

    public static LoadQuestionImpactPort.Result toResult(List<QuestionImpactPersistenceJpaAdapter.QuestionImpactDto> items) {
        return new LoadQuestionImpactPort.Result(
            toDomainModel(items.get(0))
        );
    }

    private static QuestionImpact toDomainModel(QuestionImpactPersistenceJpaAdapter.QuestionImpactDto questionImpactDto) {
        return new QuestionImpact(
            questionImpactDto.id(),
            questionImpactDto.level(),
            questionImpactDto.maturity_level(),
            null,
            questionImpactDto.quality_attribute(),
            questionImpactDto.weight()
        );
    }
}
