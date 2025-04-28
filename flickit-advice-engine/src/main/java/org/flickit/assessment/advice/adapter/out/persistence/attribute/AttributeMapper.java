package org.flickit.assessment.advice.adapter.out.persistence.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.jetbrains.annotations.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static Attribute mapToDomainModel(AttributeJpaEntity entity) {
        return new Attribute(
            entity.getId(),
            entity.getTitle());
    }

    public static AdviceAttribute toAdviceItem(AttributeJpaEntity entity, @Nullable KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new AdviceAttribute(entity.getId(), translation.titleOrDefault(entity.getTitle()));
    }

    public static AttributeTranslation getTranslation(AttributeJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new AttributeTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AttributeTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
