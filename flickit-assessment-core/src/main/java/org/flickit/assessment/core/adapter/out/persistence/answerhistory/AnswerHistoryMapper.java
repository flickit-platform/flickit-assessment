package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.port.out.answerhistory.CreateAnswerHistoryPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerHistoryMapper {

    public static AnswerHistoryJpaEntity mapCreateParamToJpaEntity(CreateAnswerHistoryPort.Param param, AssessmentResultJpaEntity assessmentResult, AnswerJpaEntity answer) {
        return new AnswerHistoryJpaEntity(
            null,
            answer,
            assessmentResult,
            param.questionnaireId(),
            param.questionId(),
            param.answerOptionId(),
            param.confidenceLevelId(),
            param.isNotApplicable(),
            param.currentUserId(),
            param.modifiedAt(),
            param.historyTypeId()
        );
    }
}
