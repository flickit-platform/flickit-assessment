package org.flickit.assessment.kit.adapter.out.persistence.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.flickit.assessment.kit.application.domain.AnswerOption;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity entity) {
        return new AnswerOption(
            entity.getId(),
            entity.getQuestionId(),
            entity.getTitle(),
            entity.getIndex()
        );
    }
}
