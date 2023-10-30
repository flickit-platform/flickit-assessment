package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.core.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.core.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;

import java.util.UUID;

public class SubjectValueJpaEntityMother {

    private static long subjectId = 134L;

    public static SubjectValueJpaEntity subjectValueWithNullMaturityLevel(AssessmentResultJpaEntity assessmentResultJpaEntity) {
        return new SubjectValueJpaEntity(
            UUID.randomUUID(),
            assessmentResultJpaEntity,
            subjectId++,
            null
        );
    }
}
