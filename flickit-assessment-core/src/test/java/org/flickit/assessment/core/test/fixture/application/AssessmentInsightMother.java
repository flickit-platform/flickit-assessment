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
            LocalDateTime.now().plusSeconds(1),
            UUID.randomUUID(),
            false
        );
    }

    public static AssessmentInsight createInitialInsightWithAssessmentResultId(UUID assessmentResultId) {
        return new AssessmentInsight(
            null,
            assessmentResultId,
            RandomStringUtils.random(50),
            LocalDateTime.now().plusSeconds(1),
            null,
            false
        );
    }

    public static AssessmentInsight createSimpleAssessmentInsight() {
        return new AssessmentInsight(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RandomStringUtils.random(50),
            LocalDateTime.now().plusSeconds(1),
            UUID.randomUUID(),
            false
        );
    }

    public static AssessmentInsight createWithMinInsightTime() {
        return new AssessmentInsight(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RandomStringUtils.random(50),
            LocalDateTime.MIN,
            UUID.randomUUID(),
            false
        );
    }
}
