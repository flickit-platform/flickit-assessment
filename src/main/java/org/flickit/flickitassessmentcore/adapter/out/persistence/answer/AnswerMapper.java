package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.application.port.out.answer.CreateAnswerPort;

public class AnswerMapper {

    public static AnswerJpaEntity mapCreateParamToJpaEntity(CreateAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionId(),
            param.answerOptionId()
        );
    }

    public static Answer mapToDomainModel(AnswerJpaEntity answerJpaEntity) {
        return new Answer(
            answerJpaEntity.getId(),
            answerJpaEntity.getAssessmentResult().getId(),
            answerJpaEntity.getQuestionId(),
            answerJpaEntity.getAnswerOptionId()
        );
    }
}
