package org.flickit.assessment.core.adapter.out.persistence.kit.measure;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.core.application.domain.Measure;
import org.flickit.assessment.data.jpa.kit.measure.MeasureJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MeasureMapper {

    public static Measure mapToDomainModel(MeasureJpaEntity entity, @Nullable KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new Measure(
            entity.getId(),
            translation.titleOrDefault(entity.getTitle()));
    }

    private static MeasureTranslation getTranslation(MeasureJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new MeasureTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, MeasureTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
