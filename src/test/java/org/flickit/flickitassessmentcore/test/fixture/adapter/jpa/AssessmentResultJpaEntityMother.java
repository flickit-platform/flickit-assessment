package org.flickit.flickitassessmentcore.test.fixture.adapter.jpa;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.test.fixture.adapter.jpa.AssessmentJpaEntityMother.assessmentEntityWithKit;

public class AssessmentResultJpaEntityMother {

    public static AssessmentResultJpaEntity validSimpleAssessmentResultEntity(Long maturityLevelId, Boolean isValid) {
        return new AssessmentResultJpaEntity(
            UUID.randomUUID(),
            assessmentEntityWithKit(),
            maturityLevelId,
            isValid,
            LocalDateTime.now()
        );
    }
}
