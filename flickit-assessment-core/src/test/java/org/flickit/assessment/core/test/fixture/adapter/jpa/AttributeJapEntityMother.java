package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class AttributeJapEntityMother {

    public static AttributeJpaEntity createAttributeEntity(Long id, Integer index, Long kitId) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new AttributeJpaEntity(
            id,
            "code" + id,
            "title" + id,
            index,
            "description" + id,
            1,
            kitId,
            creationTime,
            creationTime,
            UUID.randomUUID()
        );
    }
}
