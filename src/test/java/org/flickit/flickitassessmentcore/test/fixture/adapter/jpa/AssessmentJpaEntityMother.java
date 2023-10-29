package org.flickit.flickitassessmentcore.test.fixture.adapter.jpa;

import org.flickit.flickitassessmentcore.adapter.out.persistence.assessment.AssessmentJpaEntity;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.application.service.constant.AssessmentConstants.NOT_DELETED_DELETION_TIME;

public class AssessmentJpaEntityMother {

    private static long kitId = 134L;
    private static int assessmentCounter = 341;

    public static AssessmentJpaEntity assessmentEntityWithKit() {
        assessmentCounter++;
        return new AssessmentJpaEntity(
            UUID.randomUUID(),
            "assessment-code" + assessmentCounter,
            "assessment-title" + assessmentCounter,
            kitId++,
            AssessmentColor.getDefault().getId(),
            1L,
            LocalDateTime.now(),
            LocalDateTime.now(),
            NOT_DELETED_DELETION_TIME,
            Boolean.FALSE
        );
    }
}
