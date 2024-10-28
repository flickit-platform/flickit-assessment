package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;

import java.time.LocalDateTime;

public class AnswerRangeMapper {

    public static AnswerRangeJpaEntity toJpaEntity(CreateAnswerRangePort.Param param) {
        return new AnswerRangeJpaEntity(null,
            param.kitVersionId(),
            param.title(),
            param.reusable(),
            LocalDateTime.now(),
            LocalDateTime.now(),
            param.createdBy(),
            param.createdBy());
    }
}
