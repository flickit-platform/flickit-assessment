package org.flickit.assessment.kit.adapter.out.persistence.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.kit.application.domain.Questionnaire;
import org.flickit.assessment.kit.application.domain.dsl.QuestionnaireDslModel;

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
            null,
            JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, QuestionnaireTranslation.class),
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
            JsonUtils.toJson(questionnaire.getTranslations()),
            questionnaire.getCreationTime(),
            questionnaire.getLastModificationTime(),
            createdBy,
            createdBy
        );
    }

    public static QuestionnaireDslModel mapToDslModel(QuestionnaireJpaEntity entity) {
        return QuestionnaireDslModel.builder()
            .code(entity.getCode())
            .index(entity.getIndex())
            .title(entity.getTitle())
            .description(entity.getDescription())
            .build();
    }
}
