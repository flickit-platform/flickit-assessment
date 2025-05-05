package org.flickit.assessment.core.application.port.in.assessmentreport;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.validation.EnumValue;
import org.flickit.assessment.core.application.domain.VisibilityType;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface UpdateAssessmentReportVisibilityUseCase {

    Result updateReportVisibility(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_ASSESSMENT_REPORT_VISIBILITY_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = UPDATE_ASSESSMENT_REPORT_VISIBILITY_VISIBILITY_NOT_NULL)
        @EnumValue(enumClass = VisibilityType.class, message = UPDATE_ASSESSMENT_REPORT_VISIBILITY_VISIBILITY_INVALID)
        String visibility;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(UUID assessmentId, String visibility, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.visibility = visibility;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(String visibility, UUID linkHash) {
    }
}
