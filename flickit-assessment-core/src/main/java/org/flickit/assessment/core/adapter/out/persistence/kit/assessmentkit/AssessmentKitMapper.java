package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.KitTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AssessmentKitMapper {

    public static AssessmentKit mapToDomainModel(AssessmentKitJpaEntity entity, List<MaturityLevel> maturityLevels) {
        return new AssessmentKit(
            entity.getId(),
            entity.getTitle(),
            entity.getKitVersionId(),
            KitLanguage.valueOfById(entity.getLanguageId()),
            maturityLevels,
            entity.getIsPrivate()
        );
    }

    public static AssessmentListItem.Kit mapToAssessmentListItemKit(AssessmentKitJpaEntity kitEntity, int kitLevelEntities, KitLanguage language) {
        var kitTranslation = new KitTranslation(null, null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(kitEntity.getTranslations(), KitLanguage.class, KitTranslation.class);
            kitTranslation = translations.getOrDefault(language, kitTranslation);
        }
        return new AssessmentListItem.Kit(kitEntity.getId(),
            kitTranslation.titleOrDefault(kitEntity.getTitle()),
            kitLevelEntities);
    }
}
