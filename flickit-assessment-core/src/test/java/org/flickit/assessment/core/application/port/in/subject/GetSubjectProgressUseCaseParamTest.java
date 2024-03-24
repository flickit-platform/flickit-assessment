package org.flickit.assessment.core.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetSubjectProgressUseCaseParamTest {

    @Test
    void GetSubjectProgress_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSubjectProgressUseCase.Param(null, 1L, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void GetSubjectProgress_SubjectIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSubjectProgressUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void GetSubjectProgress_CurrentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSubjectProgressUseCase.Param(assessmentId, 123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
