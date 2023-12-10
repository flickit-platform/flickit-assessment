package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.adapter.jpa.AssessmentJpaEntityMother.assessmentEntityWithKit;

public class AssessmentResultJpaEntityMother {

    public static AssessmentResultJpaEntity validSimpleAssessmentResultEntity(Long maturityLevelId, Boolean isCalculateValid, Boolean isConfidenceValid) {
        return new AssessmentResultJpaEntity(
            UUID.randomUUID(),
            assessmentEntityWithKit(),
            maturityLevelId,
            1.0,
            isCalculateValid,
            isConfidenceValid,
            LocalDateTime.now()
        );
    }
}
