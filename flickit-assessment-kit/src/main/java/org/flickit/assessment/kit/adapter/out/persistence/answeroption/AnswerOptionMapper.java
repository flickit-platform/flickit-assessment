package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerOption;
import org.flickit.assessment.kit.application.domain.dsl.AnswerOptionDslModel;
import org.flickit.assessment.kit.application.port.out.answeroption.CreateAnswerOptionPort;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity entity) {
        return new AnswerOption(
            entity.getId(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getAnswerRangeId(),
            entity.getValue()
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
            JsonUtils.toJson(param.translation()),
            creationTime,
            creationTime,
            param.createdBy(),
            param.createdBy()
        );
    }

    public static AnswerOptionDslModel mapToDslModel(AnswerOptionJpaEntity entity) {
        return AnswerOptionDslModel.builder()
            .index(entity.getIndex())
            .caption(entity.getTitle())
            .value(entity.getValue())
            .build();
    }
}
