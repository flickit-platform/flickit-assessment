package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.kit.application.domain.Questionnaire;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    public static Questionnaire mapToDomainModel(QuestionnaireJpaEntity entity) {
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

    static QuestionnaireJpaEntity mapToJpaEntityToPersist(Questionnaire questionnaire, Long kitVersionId, UUID createdBy) {
        return new QuestionnaireJpaEntity(
            null,
            kitVersionId,
            questionnaire.getCode(),
            questionnaire.getIndex(),
            questionnaire.getTitle(),
            questionnaire.getDescription(),
            questionnaire.getCreationTime(),
            questionnaire.getLastModificationTime(),
            createdBy,
            createdBy
        );
    }
}
