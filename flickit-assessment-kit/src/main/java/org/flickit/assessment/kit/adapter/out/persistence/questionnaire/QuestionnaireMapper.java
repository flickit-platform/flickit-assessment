package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.kit.application.domain.Questionnaire;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    static Questionnaire mapToDomainModel(QuestionnaireJpaEntity entity) {
        return new Questionnaire(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getCreationTime(),
            entity.getLastModificationTime()
        );
    }
}
