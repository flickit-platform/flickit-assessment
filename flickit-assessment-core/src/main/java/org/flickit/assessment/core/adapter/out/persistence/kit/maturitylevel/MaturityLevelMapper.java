package org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity) {
        return new MaturityLevel(
            entity.getId(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getValue(),
            entity.getDescription()
        );
    }

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity, @Nullable KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new MaturityLevel(
            entity.getId(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getIndex(),
            entity.getValue(),
            translation.descriptionOrDefault(entity.getDescription())
        );
    }

    public static AssessmentListItem.MaturityLevel toAssessmentListItemMaturityLevel(MaturityLevelJpaEntity entity, KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new AssessmentListItem.MaturityLevel(entity.getId(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getValue(),
            entity.getIndex());
    }

    public static MaturityLevelTranslation getTranslation(MaturityLevelJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new MaturityLevelTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, MaturityLevelTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
