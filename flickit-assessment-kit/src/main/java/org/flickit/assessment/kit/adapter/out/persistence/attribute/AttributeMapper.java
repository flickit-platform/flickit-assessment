package org.flickit.assessment.kit.adapter.out.persistence.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJoinSubjectView;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
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
            attribute.getCreationTime(),
            attribute.getLastModificationTime(),
            attribute.getCreatedBy(),
            attribute.getLastModifiedBy(),
            subjectJpaEntity.getId());
    }

    public static AttributeDslModel mapToDslModel(AttributeJoinSubjectView view) {
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
