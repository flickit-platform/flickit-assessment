package org.flickit.assessment.core.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.report.SubjectReport;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ID_NOT_NULL;

public interface ReportSubjectUseCase {

    Result reportSubject(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @NotNull(message = REPORT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        public Param(UUID assessmentId, UUID currentUserId, Long subjectId) {
            this.assessmentId = assessmentId;
            this.currentUserId = currentUserId;
            this.subjectId = subjectId;
            this.validateSelf();
        }
    }

    record Result(SubjectReport.SubjectReportItem subject,
                         List<TopAttribute> topStrengths,
                         List<TopAttribute> topWeaknesses,
                         List<SubjectReport.AttributeReportItem> attributes,
                         int maturityLevelsCount) {

        public record TopAttribute(Long id, String title) {}
    }
}
