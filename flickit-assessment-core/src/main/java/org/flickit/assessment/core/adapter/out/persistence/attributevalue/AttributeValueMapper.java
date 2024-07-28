package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeValueMapper {

    public static AttributeValueJpaEntity mapToJpaEntity(long attributeId) {
        return new AttributeValueJpaEntity(
            null,
            null,
            attributeId,
            null,
            null
        );
    }

    public static AttributeValue mapToDomainModel(AttributeValueJpaEntity entity, AttributeJpaEntity attributeEntity) {
        var attribute = new Attribute(attributeEntity.getId(), null, null, attributeEntity.getWeight(), null);
        return new AttributeValue(
            entity.getId(),
            attribute,
            null
        );
    }
}
