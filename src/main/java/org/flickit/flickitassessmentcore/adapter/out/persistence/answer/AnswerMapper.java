package org.flickit.flickitassessmentcore.adapter.out.persistence.answer;

import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result;
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

    public static Result mapToAnswerIdAndOptionIdResult(AnswerIdAndOptionIdProjectionDto dto) {
        return new Result(
            dto.id(),
            dto.answerOptionId()
        );
    }
}
