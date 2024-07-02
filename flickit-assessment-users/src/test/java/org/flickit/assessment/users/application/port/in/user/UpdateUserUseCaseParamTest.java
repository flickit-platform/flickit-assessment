package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class UpdateUserUseCaseParamTest {

    @Test
    void testUpdateUser_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserUseCase.Param(null, "Flickit Admin",  null,null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }


    @Test
    void testUpdateUser_NullDisplayName_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserUseCase.Param(UUID.randomUUID(), null,  null, null));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_DISPLAY_NAME_NOT_BLANK);
    }

    @Test
    void testUpdateUser_EmptyDisplayName_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserUseCase.Param(UUID.randomUUID(), "", null, null));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_DISPLAY_NAME_NOT_BLANK);
    }

    @Test
    void testUpdateUser_BlankDisplayName_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserUseCase.Param(UUID.randomUUID(), "  ", null, null));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_DISPLAY_NAME_NOT_BLANK);
    }

    @Test
    void testUpdateUser_BioGreaterThanMaxSize_ErrorMessage() {
        String bio = RandomStringUtils.randomAlphabetic(401);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserUseCase.Param(UUID.randomUUID(), "Flickit Admin", bio, null));
        assertThat(throwable).hasMessage("bio: " + UPDATE_USER_BIO_SIZE_MAX);
    }

    @Test
    void testUpdateUser_NotValidLinkedinUrl_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserUseCase.Param(UUID.randomUUID(), "Flickit Admin", null, "linkedin.com/admin"));
        assertThat(throwable).hasMessage("linkedin: " + UPDATE_USER_LINKEDIN_NOT_VALID);
    }
}
