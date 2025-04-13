package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
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
            JsonUtils.toTranslations(entity.getTranslations(), AttributeTranslation.class),
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
}
