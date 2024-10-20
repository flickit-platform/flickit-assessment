package org.flickit.assessment.users.application.port.in.space;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateSpaceUseCaseParamsTest {

    @Test
    void testUpdateSpaceParam_IdIsNull_ErrorMessage() {
        String title = "test";
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSpaceUseCase.Param(null, title, currentUserId));
        assertThat(throwable).hasMessage("id: " + UPDATE_SPACE_SPACE_ID_NOT_NULL);
    }

    @Test
    void testUpdateSpaceParam_TitleLengthIsLessThanMin_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        String title = RandomStringUtils.random(2, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSpaceUseCase.Param(spaceId, title, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_SPACE_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateSpaceParam_TitleLengthIsMoreThanMax_ErrorMessage() {
        long spaceId = 0L;
        UUID currentUserId = UUID.randomUUID();
        String title = RandomStringUtils.random(101, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSpaceUseCase.Param(spaceId, title, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_SPACE_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateSpaceParam_currentUserIdIsNull_ErrorMessage() {
        long spaceId = 0L;
        String title = RandomStringUtils.random(10, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateSpaceUseCase.Param(spaceId, title, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
