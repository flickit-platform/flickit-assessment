package org.flickit.assessment.advice.adapter.out.persistence.maturitylevel;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MaturityLevelMapper {

    public static MaturityLevel mapToDomainModel(MaturityLevelJpaEntity entity, KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new MaturityLevel(
            entity.getId(),
            translation.titleOrDefault(entity.getTitle()));
    }

    private static MaturityLevelTranslation getTranslation(MaturityLevelJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new MaturityLevelTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, MaturityLevelTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
