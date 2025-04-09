package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.time.LocalDateTime;

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
            null, // TODO: Check if replacing with a valid JSON translate is needed after editing the service.
            creationTime,
            creationTime,
            null,
            null);
    }
}
