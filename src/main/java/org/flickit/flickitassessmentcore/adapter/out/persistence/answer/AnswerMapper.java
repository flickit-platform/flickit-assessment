package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultMapper;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerPort;
import org.flickit.flickitassessmentcore.domain.Answer;

public class AnswerMapper {

    public static AnswerJpaEntity mapSaveParamToJpaEntity(SaveAnswerPort.Param param) {
        return new AnswerJpaEntity(
            null,
            null,
            param.questionId(),
            param.answerOptionId()
        );
    }

    public static AnswerJpaEntity mapUpdateParamToJpaEntity(UpdateAnswerPort.Param param) {
        return new AnswerJpaEntity(
            param.id(),
            null,
            param.questionId(),
            param.answerOptionId()
        );
    }

    public static Answer mapToDomainModel(AnswerJpaEntity answerEntity) {
        return new Answer(
            answerEntity.getId(),
            AssessmentResultMapper.mapToDomainModel(answerEntity.getAssessmentResult()),
            answerEntity.getQuestionId(),
            answerEntity.getAnswerOptionId()
        );
    }
}
