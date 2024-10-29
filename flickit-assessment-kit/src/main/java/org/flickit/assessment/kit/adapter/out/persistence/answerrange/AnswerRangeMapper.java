package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
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

    public static AnswerRange toDomainModel(AnswerRangeJpaEntity entity) {
        return new AnswerRange(entity.getId(),
            entity.getTitle(),
            null);
    }
}
