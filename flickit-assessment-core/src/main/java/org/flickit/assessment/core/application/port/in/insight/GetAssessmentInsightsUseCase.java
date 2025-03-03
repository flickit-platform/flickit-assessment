package org.flickit.assessment.core.application.port.in.insight;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.AssessmentListItem;
import org.flickit.assessment.core.application.domain.insight.Insight;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentInsightsUseCase {

    Result getAssessmentInsights(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_INSIGHTS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Assessment assessment,
                  List<Subject> subjects,
                  Issues issues) {
    }

    record Assessment(UUID id,
                      String title,
                      AssessmentListItem.MaturityLevel maturityLevel,
                      Double confidenceValue,
                      boolean isCalculateValid,
                      boolean isConfidenceValid,
                      Insight insight) {
    }

    record Subject(Long id,
                   String title,
                   String description,
                   Integer index,
                   Integer weight,
                   AssessmentListItem.MaturityLevel maturityLevel,
                   Double confidenceValue,
                   Insight insight,
                   List<Attribute> attributes) {
    }

    record Attribute(Long id,
                     String title,
                     String description,
                     Integer index,
                     Integer weight,
                     AssessmentListItem.MaturityLevel maturityLevel,
                     Double confidenceValue,
                     Insight insight) {
    }

    record Issues(int notGenerated,
                  int unapproved,
                  int expired) {
    }
}
