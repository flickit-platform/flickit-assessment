package org.flickit.assessment.kit.application.port.in.question;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_DETAIL_KIT_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_QUESTION_DETAIL_QUESTION_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class GetQuestionDetailUseCaseParamTest {

    public static final Long KIT_ID = 25L;
    public static final Long QUESTION_ID = 11L;
    public static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testGetQuestionDetail_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionDetailUseCase.Param(null, QUESTION_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("kitId: " + GET_QUESTION_DETAIL_KIT_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionDetail_questionIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionDetailUseCase.Param(KIT_ID, null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("questionId: " + GET_QUESTION_DETAIL_QUESTION_ID_NOT_NULL);
    }

    @Test
    void testGetQuestionDetail_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new GetQuestionDetailUseCase.Param(KIT_ID, QUESTION_ID, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
