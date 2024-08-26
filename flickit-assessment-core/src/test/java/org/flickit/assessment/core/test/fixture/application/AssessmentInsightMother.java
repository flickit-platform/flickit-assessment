package org.flickit.assessment.core.test.fixture.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.core.application.domain.AssessmentInsight;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentInsightMother {
    public static AssessmentInsight createWithAssessmentResultId(UUID assessmentResultId) {
        return new AssessmentInsight(
            UUID.randomUUID(),
            assessmentResultId,
            RandomStringUtils.random(50),
            LocalDateTime.now(),
            UUID.randomUUID()
        );
    }
}
