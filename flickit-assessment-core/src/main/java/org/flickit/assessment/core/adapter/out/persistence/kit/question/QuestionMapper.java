package org.flickit.assessment.core.adapter.out.persistence.kit.question;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.core.application.domain.QuestionImpact;
import org.flickit.assessment.core.application.domain.Questionnaire;
import org.flickit.assessment.data.jpa.kit.question.QuestionJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionMapper {

    public static Question mapToDomainModel(Long id, List<QuestionImpact> impacts) {
        return new Question(
            id,
            null,
            null,
            null,
            null,
            impacts,
            null,
            null
        );
    }

    public static Question mapToDomainModel(QuestionJpaEntity entity, List<QuestionImpact> impacts) {
        return new Question(
            entity.getId(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getHint(),
            entity.getMayNotBeApplicable(),
            impacts,
            new Questionnaire(entity.getQuestionnaireId(), null),
            new Measure(entity.getMeasureId(), null)
        );
    }

    public static Question mapToDomainWithQuestionnaire(QuestionJpaEntity entity, Questionnaire questionnaire) {
        return new Question(
            entity.getId(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getHint(),
            entity.getMayNotBeApplicable(),
            null,
            questionnaire,
            null
        );
    }

    public static Question mapToDomainModel(QuestionJpaEntity entity, @Nullable KitLanguage language) {
        var translation = getTranslation(entity.getTranslations(), language);
        return new Question(
            entity.getId(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getIndex(),
            translation.hintOrDefault(entity.getHint()),
            entity.getMayNotBeApplicable(),
            null,
            new Questionnaire(entity.getQuestionnaireId(), null),
            new Measure(entity.getMeasureId(), null)
        );
    }

    public static QuestionTranslation getTranslation(String entityTranslation, @Nullable KitLanguage language) {
        var translation = new QuestionTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entityTranslation, KitLanguage.class, QuestionTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
