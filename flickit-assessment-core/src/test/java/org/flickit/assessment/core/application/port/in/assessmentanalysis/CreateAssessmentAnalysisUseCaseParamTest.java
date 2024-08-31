package org.flickit.assessment.core.application.port.in.assessmentanalysis;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_ASSESSMENT_ANALYSIS_ASSESSMENT_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class CreateAssessmentAiAnalysisUseCaseParamTest {

    @Test
    void tesCreateAssessmentAiAnalysisUseCaseParam_AssessmentIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAnalysisUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSMENT_ANALYSIS_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void tesCreateAssessmentAiAnalysisUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentAnalysisUseCase.Param(assessmentId, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
