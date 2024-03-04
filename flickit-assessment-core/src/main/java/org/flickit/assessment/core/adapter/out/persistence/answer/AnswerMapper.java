package org.flickit.assessment.core.adapter.out.persistence.answer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.domain.ConfidenceLevel;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;
import org.flickit.assessment.data.jpa.core.answer.AnswerJpaEntity;

import java.util.ArrayList;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerMapper {

    public static AnswerJpaEntity mapCreateParamToJpaEntity(CreateAnswerPort.Param param, UUID questionRefNum) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionnaireId(),
            param.questionId(),
            questionRefNum,
            param.answerOptionId(),
            param.confidenceLevelId(),
            param.isNotApplicable(),
            param.currentUserId(),
            param.currentUserId()
        );
    }

    public static AnswerListItem mapJpaEntityToAnswerItem(AnswerJpaEntity answer) {
        ConfidenceLevel confidenceLevel = answer.getConfidenceLevelId() != null ? ConfidenceLevel.valueOfById(answer.getConfidenceLevelId()) : null;
        return new AnswerListItem(
            answer.getId(),
            answer.getQuestionId(),
            answer.getAnswerOptionId(),
            confidenceLevel,
            answer.getIsNotApplicable()
        );
    }

    public static Answer mapToDomainModel(AnswerJpaEntity answer) {
        var answerOption = answer.getAnswerOptionId() != null ?
            new AnswerOption(answer.getAnswerOptionId(), answer.getQuestionId(), new ArrayList<>()) : null;
        return new Answer(
            answer.getId(),
            answerOption,
            answer.getQuestionId(),
            answer.getConfidenceLevelId(),
            answer.getIsNotApplicable()
        );
    }
}
