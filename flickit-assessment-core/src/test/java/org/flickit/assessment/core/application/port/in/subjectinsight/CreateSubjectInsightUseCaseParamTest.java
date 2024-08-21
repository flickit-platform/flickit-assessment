package org.flickit.assessment.core.application.port.in.subjectinsight;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSubjectInsightUseCaseParamTest {

    @Test
    void CreateSubjectInsight_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectInsightUseCase.Param(null, 1L, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_SUBJECT_INSIGHT_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void CreateSubjectInsight_SubjectIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectInsightUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("subjectId: " + CREATE_SUBJECT_INSIGHT_SUBJECT_ID_NOT_NULL);
    }

    @Test
    void CreateSubjectInsight_CurrentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSubjectInsightUseCase.Param(assessmentId, 123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
