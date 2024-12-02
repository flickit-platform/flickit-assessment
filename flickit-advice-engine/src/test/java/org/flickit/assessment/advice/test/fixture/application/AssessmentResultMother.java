package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.AssessmentResult;

import java.util.UUID;

public class AssessmentResultMother {

    private static long kitVersionId = 0L;

    public static AssessmentResult createAssessmentResult() {
        return new AssessmentResult(UUID.randomUUID(),
            ++kitVersionId,
            UUID.randomUUID());
    }
}
