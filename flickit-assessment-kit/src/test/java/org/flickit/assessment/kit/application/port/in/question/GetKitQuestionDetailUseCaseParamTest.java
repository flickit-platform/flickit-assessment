package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_QUESTION_DETAIL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_QUESTION_DETAIL_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetKitQuestionDetailUseCaseParamTest {

    @Test
    void testGetKitQuestionDetail_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitQuestionDetailUseCase.Param(null, 123L, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + GET_KIT_QUESTION_DETAIL_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetKitQuestionDetail_questionIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitQuestionDetailUseCase.Param(123L, null, currentUserId));
        assertThat(throwable).hasMessage("questionId: " + GET_KIT_QUESTION_DETAIL_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetKitQuestionDetail_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetKitQuestionDetailUseCase.Param(123L, 123L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
