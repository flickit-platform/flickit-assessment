package org.flickit.assessment.core.test.fixture.application;

import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.core.application.domain.insight.AssessmentInsight;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentInsightMother {
    public static AssessmentInsight createWithAssessmentResultId(UUID assessmentResultId) {
        return new AssessmentInsight(
            UUID.randomUUID(),
            assessmentResultId,
            RandomStringUtils.random(50),
            LocalDateTime.now().plusSeconds(1),
            LocalDateTime.now().plusSeconds(1),
            UUID.randomUUID(),
            false
        );
    }

    public static AssessmentInsight createDefaultInsightWithAssessmentResultId(UUID assessmentResultId) {
        return new AssessmentInsight(
            null,
            assessmentResultId,
            RandomStringUtils.random(50),
            LocalDateTime.now().plusSeconds(1),
            LocalDateTime.now().plusSeconds(1),
            null,
            false
        );
    }

    public static AssessmentInsight createDefaultInsightWithTimesAndApprove(LocalDateTime insightTime,
                                                                            LocalDateTime lastModificationTIme,
                                                                            boolean approved) {
        return new AssessmentInsight(
            null,
            UUID.randomUUID(),
            RandomStringUtils.random(50),
            insightTime,
            lastModificationTIme,
            null,
            approved
        );
    }

    public static AssessmentInsight createSimpleAssessmentInsight() {
        return createWithAssessmentResultId(UUID.randomUUID());
    }

    public static AssessmentInsight createWithMinInsightTime() {
        return new AssessmentInsight(
            UUID.randomUUID(),
            UUID.randomUUID(),
            RandomStringUtils.random(50),
            LocalDateTime.MIN,
            LocalDateTime.MIN,
            UUID.randomUUID(),
            false
        );
    }
}
