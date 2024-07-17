package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerHistoryMapper {

    public static AnswerHistoryJpaEntity mapCreateParamToJpaEntity(AnswerHistory answerHistory, AssessmentResultJpaEntity assessmentResult, AnswerJpaEntity answer) {
        return new AnswerHistoryJpaEntity(
            null,
            answer,
            assessmentResult,
            answerHistory.getAnswer().getQuestionId(),
            answerHistory.getAnswer().getSelectedOption().getId(),
            answerHistory.getAnswer().getConfidenceLevelId(),
            answerHistory.getAnswer().getIsNotApplicable(),
            answerHistory.getCreatedBy(),
            answerHistory.getCreationTime(),
            answerHistory.getHistoryType().ordinal()
        );
    }
}
