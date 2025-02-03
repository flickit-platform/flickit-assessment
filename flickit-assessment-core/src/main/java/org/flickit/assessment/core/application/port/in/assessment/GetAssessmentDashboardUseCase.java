package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_DASHBOARD_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentDashboardUseCase {

    Result getAssessmentDashboard(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_ASSESSMENT_DASHBOARD_ASSESSMENT_ID_NOT_NULL)
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

    record Result(Questions questions, Insights insights, Advices advices, Report report) {

        public record Questions(int total,
                                int answered,
                                int unanswered,
                                int answeredWithLowConfidence,
                                int withoutEvidence,
                                int unresolvedComments) {
        }

        public record Insights(int expected,
                               int notGenerated,
                               int unapproved,
                               int expired) {
        }

        public record Advices(int total) {
        }

        public record Report(boolean unpublished,
                             int unprovidedMetadata,
                             int totalMetadata){
        }
    }
}
