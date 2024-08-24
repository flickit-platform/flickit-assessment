package org.flickit.assessment.core.application.port.in.assessmentinsight;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.LOAD_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL;

public interface GetAssessmentInsightUseCase {

    Result getAssessmentInsight(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = LOAD_ASSESSMENT_INSIGHT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(DefaultInsight defaultInsight, AssessorInsight assessorInsight, boolean editable) {

        public record DefaultInsight(String insight) {
        }

        public record AssessorInsight(String insight, LocalDateTime creationTime, boolean isValid) {
        }
    }
}
