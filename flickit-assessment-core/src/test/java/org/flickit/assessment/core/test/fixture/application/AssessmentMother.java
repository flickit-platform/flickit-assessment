package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.domain.Assessment;
import org.flickit.assessment.core.application.domain.AssessmentColor;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentListUseCase.AssessmentListItem;

import java.time.LocalDateTime;
import java.util.UUID;

public class AssessmentMother {

    private static int counter = 341;

    public static Assessment assessment() {
        counter++;
        return new Assessment(
            UUID.randomUUID(),
            "my-assessment-" + counter,
            "My Assessment " + counter,
            AssessmentKitMother.kit(),
            AssessmentColor.BLUE.getId(),
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
            kitId,
            spaceId,
            AssessmentColor.BLUE,
            LocalDateTime.now(),
            1L,
            Boolean.TRUE,
            Boolean.TRUE
        );
    }
}
