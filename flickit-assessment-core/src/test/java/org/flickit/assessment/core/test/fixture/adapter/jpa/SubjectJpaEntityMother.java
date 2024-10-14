package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.attribute.AttributeJpaEntity;
import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.time.LocalDateTime;
import java.util.List;

public class SubjectJpaEntityMother {

    public static SubjectJpaEntity subjectWithAttributes(Long subjectId, Long kitVersionId, Integer index) {
        LocalDateTime creationTime = LocalDateTime.now();
        return new SubjectJpaEntity(
            subjectId,
            kitVersionId,
            "code" + subjectId,
            index,
            "title" + subjectId,
            "description" + subjectId,
            1,
            creationTime,
            creationTime,
            null,
            null);
    }
}
