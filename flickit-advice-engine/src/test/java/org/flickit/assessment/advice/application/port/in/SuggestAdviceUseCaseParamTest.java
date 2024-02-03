package org.flickit.assessment.advice.application.port.in;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SuggestAdviceUseCaseParamTest {

    @Test
    void testSuggestAdviceParam_AssessmentIdIsNull_ErrorMessage() {
        HashMap<Long, Long> targets = new HashMap<>();
        targets.put(1L, 2L);
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SuggestAdviceUseCase.Param(null, targets, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + SUGGEST_ADVICE_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testSuggestAdviceParam_TargetsIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SuggestAdviceUseCase.Param(assessmentId, null, currentUserId));
        assertThat(throwable).hasMessage("targets: " + SUGGEST_ADVICE_TARGETS_NOT_NULL);
    }

    @Test
    void testSuggestAdviceParam_TargetsIsLessThanMin_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        HashMap<Long, Long> targets = new HashMap<>();
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SuggestAdviceUseCase.Param(assessmentId, targets, currentUserId));
        assertThat(throwable).hasMessage("targets: " + SUGGEST_ADVICE_TARGETS_SIZE_MIN);
    }

    @Test
    void testSuggestAdviceParam_CurrentUserIdIsNull_ErrorMessage() {
        UUID assessmentId = UUID.randomUUID();
        HashMap<Long, Long> targets = new HashMap<>();
        targets.put(1L, 2L);

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new SuggestAdviceUseCase.Param(assessmentId, targets, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
