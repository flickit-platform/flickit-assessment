package org.flickit.assessment.core.adapter.out.persistence.kit.answeroption;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;
import org.flickit.assessment.common.util.JsonUtils;
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

    public static AnswerOption mapToDomainModel(AnswerOptionJpaEntity entity, @Nullable KitLanguage language) {
        var translation = getTranslation(entity.getTranslations(), language);

        return new AnswerOption(
            entity.getId(),
            entity.getIndex(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getValue());
    }

    public static AnswerOptionTranslation getTranslation(String entityTranslation, @Nullable KitLanguage language) {
        var translation = new AnswerOptionTranslation(null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entityTranslation, KitLanguage.class, AnswerOptionTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
