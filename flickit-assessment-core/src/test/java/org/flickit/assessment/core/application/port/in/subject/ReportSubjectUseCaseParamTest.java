package org.flickit.assessment.core.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.REPORT_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class ReportSubjectUseCaseParamTest {

    @Test
    void testReportSubject_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ReportSubjectUseCase.Param(null, 1L, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + REPORT_SUBJECT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testReportSubject_SubjectIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ReportSubjectUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + REPORT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void testReportSubject_CurrentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new ReportSubjectUseCase.Param(assessmentId, 1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
