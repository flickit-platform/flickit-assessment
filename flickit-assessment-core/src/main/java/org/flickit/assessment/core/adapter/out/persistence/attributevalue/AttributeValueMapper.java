package org.flickit.assessment.core.adapter.out.persistence.attributevalue;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Answer;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.MaturityLevel;
import org.flickit.assessment.data.jpa.core.attributevalue.AttributeValueJpaEntity;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.util.List;

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
        var attribute = new Attribute(attributeEntity.getId(), attributeEntity.getIndex(), attributeEntity.getTitle(), null, attributeEntity.getWeight(), null);
        return new AttributeValue(
            entity.getId(),
            attribute,
            null
        );
    }

    public static AttributeValue mapToDomainModel(AttributeValueJpaEntity entity,
                                                  Attribute attribute,
                                                  List<Answer> answers,
                                                  MaturityLevel maturityLevel) {
        return new AttributeValue(
            entity.getId(),
            attribute,
            answers,
            null,
            maturityLevel,
            entity.getConfidenceValue()
        );
    }
}
