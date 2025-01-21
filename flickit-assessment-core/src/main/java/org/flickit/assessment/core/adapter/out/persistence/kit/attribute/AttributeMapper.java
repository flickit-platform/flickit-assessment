package org.flickit.assessment.core.adapter.out.persistence.kit.attribute;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.flickit.assessment.core.application.domain.Attribute;
import org.flickit.assessment.core.application.domain.Question;
import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AttributeMapper {

    public static Attribute mapToDomainModel(AttributeJpaEntity entity) {
        return new Attribute(
            entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            null
        );
    }

    public static Attribute mapToDomainModel(AttributeJpaEntity entity, List<Question> questions) {
        return new Attribute(
            entity.getId(),
            entity.getIndex(),
            entity.getTitle(),
            entity.getDescription(),
            entity.getWeight(),
            questions
        );
    }
}
