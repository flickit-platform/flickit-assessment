package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_PUBLISH_NOT_NULL;

public interface UpdateAssessmentReportPublishStatusUseCase {

    void updateReportPublishStatus(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = UPDATE_ASSESSMENT_REPORT_PUBLISH_STATUS_PUBLISH_NOT_NULL)
        Boolean published;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, Boolean published, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.published = published;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
