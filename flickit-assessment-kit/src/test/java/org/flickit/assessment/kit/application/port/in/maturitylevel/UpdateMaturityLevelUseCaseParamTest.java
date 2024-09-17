package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

@NotNull(message = UPDATE_MATURITY_LEVEL_TITLE_NOT_NULL)
@Size(min = 3, message = UPDATE_MATURITY_LEVEL_TITLE_SIZE_MIN)
@Size(max = 100, message = UPDATE_MATURITY_LEVEL_TITLE_SIZE_MAX)
class UpdateMaturityLevelUseCaseParamTest {

    @Test
    void testUpdateMaturityLevelUseCaseParam_titleIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param(null, 2, 1, "Description", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_MATURITY_LEVEL_TITLE_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_titleIsShort_ErrorMessage() {
        String title = "       t        ";
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param(title, 2, 1, "Description", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_MATURITY_LEVEL_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_titleIsLong_ErrorMessage() {
        String title = RandomStringUtils.random(101);
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param(title, 2, 1, "Description", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_MATURITY_LEVEL_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_ValueIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param("title", 2, null, "Description", currentUserId));
        assertThat(throwable).hasMessage("value: " + UPDATE_MATURITY_LEVEL_VALUE_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_IndexIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param("title", null, 1, "Description", currentUserId));
        assertThat(throwable).hasMessage("index: " + UPDATE_MATURITY_LEVEL_INDEX_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_DescriptionIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param("title", 2, 1, null, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_MATURITY_LEVEL_DESCRIPTION_NOT_NULL);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_DescriptionIsShort_ErrorMessage() {
        var description = "          a      ";
        var currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param("title", 2, 1, description, currentUserId));
        assertThat(throwable).hasMessage("description: " + UPDATE_MATURITY_LEVEL_DESCRIPTION_SIZE_MIN);
    }

    @Test
    void testUpdateMaturityLevelUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateMaturityLevelUseCase.Param("title", 2, 1, "Description", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
