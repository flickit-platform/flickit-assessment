package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static Attribute mapToDomainModel(AttributeJpaEntity entity) {
        return new Attribute(
            entity.getId(),
            entity.getTitle(),
            entity.getWeight(),
            null
        );
    }
}
