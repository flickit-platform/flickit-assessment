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
        var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AnswerOptionTranslation.class);
        var translation = translations.getOrDefault(language, new AnswerOptionTranslation(null));

        return new AnswerOption(
            entity.getId(),
            entity.getIndex(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getValue());
    }
}
