package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.report.AssessmentReportItem;
import org.flickit.assessment.core.application.domain.report.AssessmentSubjectReportItem;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_ASSESSMENT_ID_NOT_NULL;

public interface ReportAssessmentUseCase {

    Result reportAssessment(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = REPORT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(
        AssessmentReportItem assessment,
        List<AssessmentSubjectReportItem> subjects,
        Permissions assessmentPermissions) {
    }

    record Permissions(boolean manageable, boolean exportable) {
    }
}
