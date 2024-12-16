package org.flickit.assessment.core.adapter.out.persistence.kit.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.AnswerOption;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity entity) {
        return new AnswerOption(
            entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getValue());
    }
}
