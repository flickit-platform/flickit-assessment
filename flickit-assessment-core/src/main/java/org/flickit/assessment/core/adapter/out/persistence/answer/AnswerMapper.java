package org.flickit.assessment.core.adapter.out.persistence.answer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.AnswerStatus;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerMapper {

    public static AnswerJpaEntity mapCreateParamToJpaEntity(CreateAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionnaireId(),
            param.questionId(),
            param.answerOptionId(),
            param.confidenceLevelId(),
            param.isNotApplicable(),
            param.status().getId(),
            param.currentUserId(),
            param.currentUserId()
        );
    }

    public static Answer mapToDomainModel(AnswerJpaEntity answer) {
        var answerOption = answer.getAnswerOptionId() != null ?
            new AnswerOption(answer.getAnswerOptionId(), null, null, null) : null;
        return new Answer(
            answer.getId(),
            answerOption,
            answer.getQuestionId(),
            answer.getConfidenceLevelId(),
            answer.getIsNotApplicable(),
            AnswerStatus.valueOfById(answer.getStatus())
        );
    }

    public static Answer mapToDomainModel(AnswerJpaEntity answer, AnswerOption answerOption) {
        return new Answer(
            answer.getId(),
            answerOption,
            answer.getQuestionId(),
            answer.getConfidenceLevelId(),
            answer.getIsNotApplicable(),
            AnswerStatus.valueOfById(answer.getStatus())
        );
    }
}
