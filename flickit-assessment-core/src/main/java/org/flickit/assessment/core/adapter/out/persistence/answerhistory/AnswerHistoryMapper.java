package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.user.UserMapper;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.HistoryType;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerHistoryMapper {

    public static AnswerHistoryJpaEntity mapCreateParamToJpaEntity(AnswerHistory answerHistory, AssessmentResultJpaEntity assessmentResult, AnswerJpaEntity answer) {
        return new AnswerHistoryJpaEntity(
            null,
            answer,
            assessmentResult,
            answerHistory.getAnswer().getQuestionId(),
            answerHistory.getAnswer().getSelectedOption() != null ?
                answerHistory.getAnswer().getSelectedOption().getId() :
                null,
            answerHistory.getAnswer().getConfidenceLevelId(),
            answerHistory.getAnswer().getIsNotApplicable(),
            answerHistory.getCreatedBy().getId(),
            answerHistory.getCreationTime(),
            answerHistory.getHistoryType().ordinal()
        );
    }

    public static AnswerHistory mapToDomainModel(AnswerHistoryJpaEntity entity, UserJpaEntity createdBy) {
        return new AnswerHistory(
            entity.getId(),
            mapToAnswer(entity),
            entity.getAssessmentResult().getId(),
            UserMapper.mapToFullDomain(createdBy),
            entity.getCreationTime(),
            HistoryType.values()[entity.getType()]);
    }

    private static Answer mapToAnswer(AnswerHistoryJpaEntity entity) {
        return new Answer(
            entity.getAnswer().getId(),
            entity.getAnswerOptionId() != null ?
                new AnswerOption(entity.getAnswerOptionId(),
                    null,
                    null,
                    null) :
                null,
            entity.getQuestionId(),
            entity.getConfidenceLevelId(),
            entity.getIsNotApplicable());
    }
}
