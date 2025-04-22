package org.flickit.assessment.core.adapter.out.persistence.kit.questionnaire;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.QuestionnaireTranslation;
import org.flickit.assessment.common.application.domain.kit.translation.SubjectTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.Questionnaire;
import org.flickit.assessment.core.application.domain.QuestionnaireListItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireListItemView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectWithQuestionnaireIdView;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuestionnaireMapper {

    public static QuestionnaireListItem mapToListItem(QuestionnaireListItemView questionnaireView,
                                                      List<SubjectWithQuestionnaireIdView> subjectsView,
                                                      int answerCount,
                                                      int nextQuestion,
                                                      KitLanguage language) {
        var questionnaireEntity = questionnaireView.getQuestionnaire();
        var translation = getTranslation(questionnaireEntity, language);

        List<QuestionnaireListItem.Subject> subjects = List.of();
        if (subjectsView != null)
            subjects = subjectsView.stream()
                .map(s -> {
                    var subjectTranslation = new SubjectTranslation(null, null);
                    if (language != null) {
                        var translations = JsonUtils.fromJsonToMap(s.getTranslations(), KitLanguage.class, SubjectTranslation.class);
                        subjectTranslation = translations.getOrDefault(language, subjectTranslation);
                    }
                    return new QuestionnaireListItem.Subject(s.getId(), subjectTranslation.titleOrDefault(s.getTitle()));
                })
                .toList();
        int progress = (int) Math.floor(((double) answerCount / questionnaireView.getQuestionCount()) * 100);

        return new QuestionnaireListItem(
            questionnaireEntity.getId(),
            translation.titleOrDefault(questionnaireEntity.getTitle()),
            translation.descriptionOrDefault(questionnaireEntity.getDescription()),
            questionnaireEntity.getIndex(),
            questionnaireView.getQuestionCount(),
            answerCount,
            nextQuestion,
            progress,
            subjects,
            null
        );
    }

    public static Questionnaire mapToDomainModel(QuestionnaireJpaEntity entity) {
        return new Questionnaire(entity.getId(), entity.getTitle());
    }

    public static QuestionnaireReportItem mapToReportItem(QuestionnaireListItemView itemView, @Nullable KitLanguage language) {
        QuestionnaireJpaEntity questionnaire = itemView.getQuestionnaire();
        var translation = getTranslation(questionnaire, language);

        return new QuestionnaireReportItem(questionnaire.getId(),
            translation.titleOrDefault(questionnaire.getTitle()),
            translation.descriptionOrDefault(questionnaire.getDescription()),
            questionnaire.getIndex(),
            itemView.getQuestionCount());
    }

    private static QuestionnaireTranslation getTranslation(QuestionnaireJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new QuestionnaireTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, QuestionnaireTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
