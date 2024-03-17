package org.flickit.assessment.core.adapter.out.persistence.qualityattributevalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.QualityAttribute;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;
import org.flickit.assessment.data.jpa.core.attributevalue.QualityAttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QualityAttributeValueMapper {

    public static QualityAttributeValueJpaEntity mapToJpaEntity(UUID attributeRefNum) {
        return new QualityAttributeValueJpaEntity(
            null,
            null,
            attributeRefNum,
            null,
            null
        );
    }

    public static QualityAttributeValue mapToDomainModel(QualityAttributeValueJpaEntity entity, AttributeJpaEntity attributeEntity) {
        var attribute = new QualityAttribute(attributeEntity.getId(), attributeEntity.getWeight(), null);
        return new QualityAttributeValue(
            entity.getId(),
            attribute,
            null
        );
    }
}
