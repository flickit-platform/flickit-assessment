package org.flickit.assessment.advice.adapter.out.persistence.advicequestion;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.port.in.advice.CreateAdviceUseCase.AdviceQuestion;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;
import org.flickit.assessment.data.jpa.advice.advicequestion.AdviceQuestionJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AdviceQuestionMapper {

    public static AdviceQuestionJpaEntity mapToEntity(AdviceQuestion adviceQuestion, AdviceJpaEntity advice) {
        return new AdviceQuestionJpaEntity(
            null,
            advice,
            adviceQuestion.questionId(),
            adviceQuestion.recommendedOptionIndex(),
            adviceQuestion.benefit());
    }
}
