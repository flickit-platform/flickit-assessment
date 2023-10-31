package org.flickit.assessment.core.adapter.out.persistence.answer;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.core.application.port.in.answer.GetAnswerListUseCase.AnswerListItem;
import org.flickit.assessment.core.application.port.out.answer.CreateAnswerPort;

import java.util.ArrayList;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerMapper {

    public static AnswerJpaEntity mapCreateParamToJpaEntity(CreateAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionnaireId(),
            param.questionId(),
            param.answerOptionId(),
            param.isNotApplicable()
        );
    }

    public static AnswerListItem mapJpaEntityToAnswerItem(AnswerJpaEntity answer) {
        return new AnswerListItem(
            answer.getId(),
            answer.getQuestionId(),
            answer.getAnswerOptionId(),
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
            answer.getIsNotApplicable()
        );
    }
}
