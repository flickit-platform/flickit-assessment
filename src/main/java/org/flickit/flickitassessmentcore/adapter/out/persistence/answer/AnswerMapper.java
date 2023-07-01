package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;

public class AnswerMapper {

    public static AnswerJpaEntity mapSaveParamToJpaEntity(SaveAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionId(),
            param.answerOptionId()
        );
    }
}
