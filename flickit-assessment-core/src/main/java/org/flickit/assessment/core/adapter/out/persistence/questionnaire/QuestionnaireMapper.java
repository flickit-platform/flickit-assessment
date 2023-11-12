package org.flickit.assessment.core.adapter.out.persistence.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.kit.domain.Questionnaire;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    public static Questionnaire mapToKitDomainModel(QuestionnaireJpaEntity entity) {
        return new Questionnaire(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getIndex()
        );
    }
}
