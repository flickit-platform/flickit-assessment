package org.flickit.assessment.core.adapter.out.persistence.answerhistory;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.core.adapter.out.persistence.kit.answeroption.AnswerOptionMapper;
import org.flickit.assessment.core.adapter.out.persistence.user.UserMapper;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerHistory;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.domain.HistoryType;
import org.flickit.assessment.core.application.port.out.answerhistory.LoadAnswerHistoryListPort;
import org.flickit.assessment.data.jpa.core.answerhistory.AnswerHistoryJpaEntity;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.data.jpa.users.user.UserJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerHistoryMapper {

    public static AnswerHistory mapToDomainModel(AnswerHistoryJpaEntity entity,
                                                 UserJpaEntity createdBy,
                                                 AnswerOptionJpaEntity selectedOption,
                                                 @Nullable KitLanguage language) {
        return new AnswerHistory(
            entity.getId(),
            mapToAnswer(entity, selectedOption, language),
            entity.getAssessmentResult().getId(),
            UserMapper.mapToFullDomain(createdBy),
            entity.getCreationTime(),
            HistoryType.values()[entity.getType()]);
    }

    public static LoadAnswerHistoryListPort.Result mapToResul(AnswerHistoryJpaEntity entity,
                                                              UserJpaEntity createdBy,
                                                              AnswerOptionJpaEntity selectedOption) {
        return new LoadAnswerHistoryListPort.Result(
            mapToAnswer(entity, selectedOption, null),
            UserMapper.mapToFullDomain(createdBy),
            entity.getCreationTime(),
            entity.getAnswerOptionId(),
            entity.getAnswerOptionIndex());
    }

    private static Answer mapToAnswer(AnswerHistoryJpaEntity entity,
                                      AnswerOptionJpaEntity selectedOption,
                                      @Nullable KitLanguage language) {
        return new Answer(
            entity.getAnswer().getId(),
            selectedOption != null ? AnswerOptionMapper.mapToDomainModel(selectedOption, language) : null,
            entity.getQuestionId(),
            entity.getConfidenceLevelId(),
            entity.getIsNotApplicable(),
            entity.getStatus() != null ? AnswerStatus.valueOfById(entity.getStatus()) : null);
    }
}
