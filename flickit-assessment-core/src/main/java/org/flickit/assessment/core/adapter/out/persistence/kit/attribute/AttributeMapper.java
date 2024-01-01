package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static QualityAttribute mapToDomainModel(AttributeJpaEntity entity) {
        return new QualityAttribute(
            entity.getId(),
            entity.getWeight(),
            null
        );
    }
}
