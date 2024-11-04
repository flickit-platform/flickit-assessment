package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity entity) {
        return new AnswerOption(
            entity.getId(),
            entity.getTitle(),
            entity.getIndex()
            );
    }

    public static AnswerOptionJpaEntity mapToJpaEntity(CreateAnswerOptionPort.Param param) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new AnswerOptionJpaEntity(
            null,
            param.kitVersionId(),
            param.index(),
            param.title(),
            param.answerRangeId(),
            param.value(),
            creationTime,
            creationTime,
            param.createdBy(),
            param.createdBy()
        );
    }
}
