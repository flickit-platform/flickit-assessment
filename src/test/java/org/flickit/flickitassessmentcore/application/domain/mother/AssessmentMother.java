package org.flickit.flickitassessmentcore.application.domain.mother;

import org.flickit.flickitassessmentcore.application.domain.Assessment;
import org.flickit.flickitassessmentcore.application.domain.AssessmentColor;
import org.flickit.flickitassessmentcore.application.port.in.assessment.CheckComparativeAssessmentsUseCase;

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
            1L,
            LocalDateTime.now(),
            LocalDateTime.now()
        );
    }

    public static CheckComparativeAssessmentsUseCase.AssessmentListItem createComparativeAssessmentListItem(Long kitId) {
        counter++;
        return new CheckComparativeAssessmentsUseCase.AssessmentListItem(
            UUID.randomUUID(),
            "My Assessment " + counter,
            kitId,
            1L,
            1,
            LocalDateTime.now(),
            1L,
            Boolean.TRUE,
            new CheckComparativeAssessmentsUseCase.Progress(5)
        );
    }
}
