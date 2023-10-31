package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.core.adapter.out.persistence.assessmentresult.AssessmentResultJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.test.fixture.adapter.jpa.AssessmentJpaEntityMother.assessmentEntityWithKit;

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
