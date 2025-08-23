package org.flickit.assessment.advice.test.fixture.application;

import org.flickit.assessment.advice.application.domain.Assessment;
import java.util.UUID;

public class AssessmentMother {

    private static int counter = 341;

    public static Assessment simpleAssessment() {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "My Assessment " + counter,
            "ShortTitle"
        );
    }
}
