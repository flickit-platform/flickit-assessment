package org.flickit.assessment.advice.adapter.out.persistence.question;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {

    public static AdviceQuestion toAdviceItem(QuestionJpaEntity entity, KitLanguage language) {
        var translation = getTranslation(entity, language);

        return new AdviceQuestion(entity.getId(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getIndex());
    }

    public static QuestionTranslation getTranslation(QuestionJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new QuestionTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, QuestionTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
