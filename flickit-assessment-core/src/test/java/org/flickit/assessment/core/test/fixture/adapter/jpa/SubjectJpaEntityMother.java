package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class SubjectJpaEntityMother {

    private static long subjectId = 134L;

    public static SubjectJpaEntity subjectWithAttributes(UUID subjectRefNum, Integer index, List<AttributeJpaEntity> attributes) {
        LocalDateTime creationTime = LocalDateTime.now();
        subjectId++;
        return new SubjectJpaEntity(
            subjectId,
            subjectRefNum,
            "code" + subjectId,
            index,
            "title" + subjectId,
            "description" + subjectId,
            1,
            1L,
            creationTime,
            creationTime,
            null,
            null,
            attributes
        );
    }
}
