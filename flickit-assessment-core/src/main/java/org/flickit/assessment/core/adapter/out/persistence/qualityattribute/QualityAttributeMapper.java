package org.flickit.assessment.core.adapter.out.persistence.qualityattribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.data.jpa.qualityattribute.QualityAttributeJpaEntity;
import org.flickit.assessment.kit.domain.Attribute;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QualityAttributeMapper {

    public static Attribute mapToKitDomainModel(QualityAttributeJpaEntity entity) {
        return new Attribute(
            entity.getCode(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getIndex(),
            entity.getSubject().getCode(),
            entity.getWeight()
        );
    }
}
