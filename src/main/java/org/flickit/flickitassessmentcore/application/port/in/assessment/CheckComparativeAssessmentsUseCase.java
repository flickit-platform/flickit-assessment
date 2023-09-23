package org.flickit.flickitassessmentcore.application.port.in.assessment;

import jakarta.validation.constraints.NotEmpty;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENT_LIST_NOT_NULL;

public interface CheckComparativeAssessmentsUseCase {

    List<AssessmentListItem> checkComparativeAssessments(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotEmpty(message = CHECK_COMPARATIVE_ASSESSMENTS_ASSESSMENT_LIST_NOT_NULL)
        List<UUID> assessmentIds;

        public Param(List<UUID> assessmentIds) {
            this.assessmentIds = assessmentIds;
            this.validateSelf();
        }
    }

    record AssessmentListItem(
        UUID id,
        String title,
        Long assessmentKitId,
        Long spaceId,
        int colorId,
        LocalDateTime lastModificationTime,
        Long maturityLevelId,
        boolean isCalculateValid,
        Progress progress
    ) {
    }

    record Progress(int totalAnsweredQuestionNumber) {
    }
}
