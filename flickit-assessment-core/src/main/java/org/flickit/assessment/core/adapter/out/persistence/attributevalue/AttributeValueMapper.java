package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.adapter.out.persistence.kit.attribute.AttributeMapper;
import org.flickit.assessment.core.adapter.out.persistence.kit.maturitylevel.MaturityLevelMapper;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

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
        var attribute = new Attribute(attributeEntity.getId(), attributeEntity.getTitle(), null, attributeEntity.getWeight(), null);
        return new AttributeValue(
            entity.getId(),
            attribute,
            null
        );
    }

    public static AttributeValue mapToDomainModel(AttributeValueJpaEntity entity, AttributeJpaEntity attributeEntity, MaturityLevelJpaEntity maturityLevelEntity) {
        var attribute = AttributeMapper.mapToDomainModel(attributeEntity);
        var maturityLevel = MaturityLevelMapper.mapToDomainModel(maturityLevelEntity, null);
        return new AttributeValue(
            entity.getId(),
            attribute,
            null,
            null,
            maturityLevel,
            entity.getConfidenceValue()
        );
    }
}
