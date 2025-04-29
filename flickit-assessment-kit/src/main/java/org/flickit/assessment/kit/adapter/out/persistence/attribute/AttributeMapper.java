package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import jakarta.annotation.Nullable;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;
import org.flickit.assessment.common.util.JsonUtils;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJoinAttributeView;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;
import org.flickit.assessment.kit.application.domain.Attribute;
import org.flickit.assessment.kit.application.domain.dsl.AttributeDslModel;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static Attribute mapToDomainModel(AttributeJpaEntity entity) {
        return new Attribute(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getWeight(),
            JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AttributeTranslation.class),
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy()
        );
    }

    public static Attribute mapToDomainModel(AttributeJpaEntity entity, KitLanguage language) {
        var translation = getTranslation(entity, language);
        return new Attribute(
            entity.getId(),
            entity.getCode(),
            translation.titleOrDefault(entity.getTitle()),
            entity.getIndex(),
            translation.descriptionOrDefault(entity.getDescription()),
            entity.getWeight(),
            null,
            entity.getCreationTime(),
            entity.getLastModificationTime(),
            entity.getCreatedBy(),
            entity.getLastModifiedBy()
        );
    }

    public static AttributeJpaEntity mapToJpaEntity(Attribute attribute, SubjectJpaEntity subjectJpaEntity) {
        return new AttributeJpaEntity(
            null,
            subjectJpaEntity.getKitVersionId(),
            attribute.getCode(),
            attribute.getIndex(),
            attribute.getTitle(),
            attribute.getDescription(),
            attribute.getWeight(),
            JsonUtils.toJson(attribute.getTranslations()),
            attribute.getCreationTime(),
            attribute.getLastModificationTime(),
            attribute.getCreatedBy(),
            attribute.getLastModifiedBy(),
            subjectJpaEntity.getId());
    }

    public static AttributeDslModel mapToDslModel(SubjectJoinAttributeView view) {
        return AttributeDslModel.builder()
            .subjectCode(view.getSubject().getCode())
            .code(view.getAttribute().getCode())
            .index(view.getAttribute().getIndex())
            .title(view.getAttribute().getTitle())
            .description(view.getAttribute().getDescription())
            .weight(view.getAttribute().getWeight())
            .build();
    }

    private static AttributeTranslation getTranslation(AttributeJpaEntity entity, @Nullable KitLanguage language) {
        var translation = new AttributeTranslation(null, null);
        if (language != null) {
            var translations = JsonUtils.fromJsonToMap(entity.getTranslations(), KitLanguage.class, AttributeTranslation.class);
            translation = translations.getOrDefault(language, translation);
        }
        return translation;
    }
}
