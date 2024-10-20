package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

public class AttributeJapEntityMother {

    public static AttributeJpaEntity createAttributeEntity(Long id, Integer index, Long kitVersionId, long subjectId) {
        LocalDateTime creationTime = LocalDateTime.now();
        UUID createdBy = UUID.randomUUID();
        return new AttributeJpaEntity(
            id,
            kitVersionId,
            "code" + id,
            index,
            "title" + id,
            "description" + id,
            1,
            creationTime,
            creationTime,
            createdBy,
            createdBy,
            subjectId);
    }
}
