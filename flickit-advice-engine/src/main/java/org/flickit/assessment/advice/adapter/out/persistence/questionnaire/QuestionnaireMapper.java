package org.flickit.assessment.advice.adapter.out.persistence.questionnaire;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    public static AdviceQuestionnaire toAdviceItem(QuestionnaireJpaEntity entity, KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new AdviceQuestionnaire(entity.getId(), translation.titleOrDefault(entity.getTitle()));
    }

    public static QuestionnaireTranslation getTranslation(QuestionnaireJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new QuestionnaireTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, QuestionnaireTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
