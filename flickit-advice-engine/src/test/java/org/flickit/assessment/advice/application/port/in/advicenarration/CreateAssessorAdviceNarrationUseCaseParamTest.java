package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.*;

class CreateAssessorAdviceNarrationUseCaseParamTest {

    @Test
    void testCreateAssessorAdviceNarration_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessorAdviceNarrationUseCase.Param(null, "assessorNarration",currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessorAdviceNarration_assessorNarrationIsGreaterThanMaxSize_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        String assessorNarration = RandomStringUtils.randomAlphabetic(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, assessorNarration,currentUserId));
        assertThat(throwable).hasMessage("assessorNarration: " + CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX);
    }

    @Test
    void testCreateAssessorAdviceNarration_currentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, "assessorNarration",null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

}
