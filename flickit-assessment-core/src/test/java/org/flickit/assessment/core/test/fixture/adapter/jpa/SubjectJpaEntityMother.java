package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.kit.subject.SubjectJpaEntity;

import java.time.LocalDateTime;

public class SubjectJpaEntityMother {

    public static SubjectJpaEntity createSubject(Long subjectId, Integer index) {
        return new SubjectJpaEntity(
            subjectId,
            "code" + subjectId,
            "title" + subjectId,
            "description" + subjectId,
            LocalDateTime.now(),
            LocalDateTime.now(),
            1L,
            index
        );
    }
}
