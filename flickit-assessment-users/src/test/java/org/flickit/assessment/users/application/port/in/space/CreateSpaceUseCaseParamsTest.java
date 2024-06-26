package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateSpaceUseCaseParamsTest {

    @Test
    void testCreateSpace_TitleIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSpaceUseCase.Param(null, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SPACE_TITLE_NOT_BLANK);
    }

    @Test
    void testCreateSpace_TitleLengthIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String title = RandomStringUtils.random(2, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSpaceUseCase.Param(title, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SPACE_TITLE_SIZE_MIN);
    }

    @Test
    void testCreateSpace_TitleLengthIsMoreThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String title = RandomStringUtils.random(101, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSpaceUseCase.Param(title, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_SPACE_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateSpace_currentUserIdIsNull_ErrorMessage() {
        String title = RandomStringUtils.random(10, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateSpaceUseCase.Param(title, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
