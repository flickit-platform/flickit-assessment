package org.flickit.assessment.advice.adapter.out.persistence.answeroption;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AnswerOptionTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.answeroption.AnswerOptionJpaEntity;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AnswerOptionMapper {

    public static AdviceOption toAdviceItem(AnswerOptionJpaEntity entity, KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new AdviceOption(entity.getIndex(), translation.titleOrDefault(entity.getTitle()));
    }

    public static AnswerOptionTranslation getTranslation(AnswerOptionJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new AnswerOptionTranslation(null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AnswerOptionTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
