package org.flickit.flickitassessmentcore.application.port.in.subject;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class GetSubjectProgressUseCaseParamTest {

    @Test
    void GetSubjectProgress_AssessmentIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSubjectProgressUseCase.Param(null, 1L));
        assertThat(throwable).hasMessage("assessmentId: " + GET_SUBJECT_PROGRESS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void GetSubjectProgress_SubjectIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetSubjectProgressUseCase.Param(assessmentId, null));
        assertThat(throwable).hasMessage("subjectId: " + GET_SUBJECT_PROGRESS_SUBJECT_ID_NOT_NULL);
    }
}
