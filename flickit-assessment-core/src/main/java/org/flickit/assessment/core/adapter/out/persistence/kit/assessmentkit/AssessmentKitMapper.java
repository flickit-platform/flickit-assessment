package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.QuestionnaireReportItem;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitMapper {

    public static AssessmentKit mapToDomainModel(AssessmentKitJpaEntity entity) {
        return new AssessmentKit(
            entity.getId(),
            entity.getTitle(),
            entity.getKitVersionId(),
            KitLanguage.valueOfById(entity.getLanguageId()),
            null,
            entity.getIsPrivate()
        );
    }

    public static AssessmentReportItem.AssessmentKitItem mapToReportItem(AssessmentKitJpaEntity entity,
                                                                         List<MaturityLevel> maturityLevels,
                                                                         List<QuestionnaireReportItem> questionnaireReportItems,
                                                                         List<Measure> measures,
                                                                         KitLanguage language) {
        var translation = getTranslation(entity, language);

        int questionsCount = questionnaireReportItems.stream()
            .mapToInt(QuestionnaireReportItem::questionCount)
            .sum();
        return new AssessmentReportItem.AssessmentKitItem(entity.getId(),
            translation.titleOrDefault(entity.getTitle()),
            maturityLevels.size(),
            questionsCount,
            maturityLevels,
            questionnaireReportItems,
            measures);
    }

    private static KitTranslation getTranslation(AssessmentKitJpaEntity assessmentKitEntity, @Nullable KitLanguage language) {
        var translation = new KitTranslation(null, null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(assessmentKitEntity.getTranslations(), KitLanguage.class, KitTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
