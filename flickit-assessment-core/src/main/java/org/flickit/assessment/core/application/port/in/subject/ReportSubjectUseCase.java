package org.flickit.assessment.core.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.report.SubjectAttributeReportItem;
import org.flickit.assessment.core.application.domain.report.SubjectReportItem;
import org.flickit.assessment.core.application.domain.report.TopAttribute;

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

        @NotNull(message = REPORT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        public Param(UUID assessmentId, Long subjectId, UUID currentUserId) {
            this.assessmentId = assessmentId;
            this.subjectId = subjectId;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(SubjectReportItem subject,
                  List<TopAttribute> topStrengths,
                  List<TopAttribute> topWeaknesses,
                  List<SubjectAttributeReportItem> attributes,
                  int maturityLevelsCount) {
    }
}
