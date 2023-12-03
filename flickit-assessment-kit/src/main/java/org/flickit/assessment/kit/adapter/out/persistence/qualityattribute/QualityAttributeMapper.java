package org.flickit.assessment.kit.adapter.out.persistence.qualityattribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.kit.qualityattribute.QualityAttributeJpaEntity;
import org.flickit.assessment.kit.application.domain.Attribute;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QualityAttributeMapper {

    public static Attribute mapToDomain(QualityAttributeJpaEntity entity) {
        return new Attribute(
            entity.getId(),
            entity.getCode(),
            entity.getTitle(),
            entity.getIndex(),
            entity.getDescription(),
            entity.getWeight(),
            entity.getCreationTime(),
            entity.getLastModificationDate()
        );
    }
}
