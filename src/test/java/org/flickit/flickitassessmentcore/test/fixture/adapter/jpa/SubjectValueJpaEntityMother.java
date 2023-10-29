package org.flickit.flickitassessmentcore.test.fixture.adapter.jpa;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue.SubjectValueJpaEntity;

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
