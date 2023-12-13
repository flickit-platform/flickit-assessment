package org.flickit.assessment.core.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.core.application.domain.report.SubjectReport;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ID_NOT_NULL;

public interface ReportSubjectUseCase {

    SubjectReport reportSubject(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = REPORT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        public Param(UUID assessmentId, Long subjectId) {
            this.assessmentId = assessmentId;
            this.subjectId = subjectId;
            this.validateSelf();
        }
    }
}
