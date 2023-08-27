package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.GetAnswerListUseCase.AnswerItem;
import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;

public class AnswerMapper {

    public static AnswerJpaEntity mapCreateParamToJpaEntity(CreateAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionnaireId(),
            param.questionId(),
            param.answerOptionId()
        );
    }

    public static AnswerItem mapJpaEntityToAnswerItem(AnswerJpaEntity answer) {
        return new AnswerItem(
            answer.getId(),
            answer.getQuestionId(),
            answer.getAnswerOptionId()
        );
    }
}
