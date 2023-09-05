package org.flickit.flickitassessmentcore.application.port.in.subject;

import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.report.SubjectReport;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.REPORT_SUBJECT_ID_NOT_NULL;

public interface ReportSubjectUseCase {

    SubjectReport reportSubject(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = REPORT_SUBJECT_ID_NOT_NULL)
        Long subjectId;

        public Param(Long subjectId) {
            this.subjectId = subjectId;
            this.validateSelf();
        }
    }
}
