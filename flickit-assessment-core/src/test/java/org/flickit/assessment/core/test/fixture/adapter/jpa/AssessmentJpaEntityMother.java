package org.flickit.assessment.core.test.fixture.adapter.jpa;

import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;

public class AssessmentJpaEntityMother {

    private static long kitId = 134L;
    private static int assessmentCounter = 341;

    public static AssessmentJpaEntity assessmentEntityWithKit() {
        assessmentCounter++;
        UUID createdBy = UUID.randomUUID();
        return new AssessmentJpaEntity(
            UUID.randomUUID(),
            "assessment-code" + assessmentCounter,
            "assessment-title" + assessmentCounter,
            kitId++,
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NOT_DELETED_DELETION_TIME,
            Boolean.FALSE,
            createdBy,
            createdBy
        );
    }
}
