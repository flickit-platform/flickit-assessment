package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

public class SubjectJpaEntityMother {

    public static SubjectJpaEntity subjectWithAttributes(Long subjectId, Integer index, List<AttributeJpaEntity> attributes) {
        return new SubjectJpaEntity(
            subjectId,
            "code" + subjectId,
            "title" + subjectId,
            "description" + subjectId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            index,
            attributes
        );
    }
}
