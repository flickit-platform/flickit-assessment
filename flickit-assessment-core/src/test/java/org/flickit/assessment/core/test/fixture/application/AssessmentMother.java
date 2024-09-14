package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.domain.Space;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

public class AssessmentMother {

    private static int counter = 341;

    public static Assessment assessment() {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            "Short title" + counter,
            AssessmentKitMother.kit(),
            new Space(123L, "title"),
            LocalDateTime.now(),
            LocalDateTime.now(),
            0L,
            false,
            UUID.randomUUID()
        );
    }

    public static AssessmentListItem assessmentListItem(Long spaceId, Long kitId) {
        counter++;
        return new AssessmentListItem(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            new AssessmentListItem.Kit(kitId, "kitTitle"+kitId, 2),
            new AssessmentListItem.Space(spaceId, "spaceTitle"),
            LocalDateTime.now(),
            new AssessmentListItem.MaturityLevel(counter, "levelTitle" + counter, 1, 2),
            new Random().nextDouble() * 100,
            Boolean.TRUE,
            Boolean.TRUE,
            Boolean.FALSE,
            true
        );
    }
}
