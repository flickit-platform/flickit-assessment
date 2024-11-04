package org.flickit.assessment.kit.adapter.out.persistence.answerrange;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answerrange.AnswerRangeJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.AnswerRange;
import org.flickit.assessment.kit.application.port.out.answerrange.CreateAnswerRangePort;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerRangeMapper {

    public static AnswerRangeJpaEntity toJpaEntity(CreateAnswerRangePort.Param param) {
        var creationTime = LocalDateTime.now();
        return new AnswerRangeJpaEntity(null,
            param.kitVersionId(),
            param.title(),
            param.reusable(),
            creationTime,
            creationTime,
            param.createdBy(),
            param.createdBy());
    }

    public static AnswerRange toDomainModel(AnswerRangeJpaEntity entity, List<AnswerOption> answerOptions) {
        return new AnswerRange(entity.getId(),
            entity.getTitle(),
            answerOptions);
    }
}
