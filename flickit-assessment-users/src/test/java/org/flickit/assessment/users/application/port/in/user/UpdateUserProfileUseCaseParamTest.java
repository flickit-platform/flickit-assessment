package org.flickit.assessment.users.application.port.in.user;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateUserProfileUseCaseParamTest {

    @Test
    void testUpdateUserProfileParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(null, "Flickit Admin", "bio", "linkedin.com"));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    @Test
    void testUpdateUserProfileParam_NullDisplayName_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, null, "bio", "linkedin.com"));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_PROFILE_DISPLAY_NAME_NOT_NULL);
    }

    @Test
    void testUpdateUserProfileParam_BlankDisplayName_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, "  ", "bio", "linkedin.com"));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_PROFILE_DISPLAY_NAME_SIZE_MIN);
    }

    @Test
    void testUpdateUserProfileParam_DisplayNameLessThanMinSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, "di", "bio", "linkedin.com"));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_PROFILE_DISPLAY_NAME_SIZE_MIN);
    }

    @Test
    void testUpdateUserProfileParam_DisplayNameGreaterThanMaxSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        String displayName = RandomStringUtils.randomAlphabetic(51);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, displayName, "bio", "linkedin.com"));
        assertThat(throwable).hasMessage("displayName: " + UPDATE_USER_PROFILE_DISPLAY_NAME_SIZE_MAX);
    }

    @Test
    void testUpdateUserProfileParam_BioIsNull_Success() {
        UUID id = UUID.randomUUID();
        assertDoesNotThrow(
            () -> new UpdateUserProfileUseCase.Param(id, "Flickit Admin", null, "linkedin.com"));
    }

    @Test
    void testUpdateUserProfileParam_BioLessThanMinSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, "Flickit Admin", "bi", "linkedin.com"));
        assertThat(throwable).hasMessage("bio: " + UPDATE_USER_PROFILE_BIO_SIZE_MIN);
    }

    @Test
    void testUpdateUserProfileParam_BioGreaterThanMaxSize_ErrorMessage() {
        UUID id = UUID.randomUUID();
        String bio = RandomStringUtils.randomAlphabetic(201);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, "Flickit Admin", bio, "linkedin.com"));
        assertThat(throwable).hasMessage("bio: " + UPDATE_USER_PROFILE_BIO_SIZE_MAX);
    }

    @Test
    void testUpdateUserProfileParam_NotValidLinkedinUrl_ErrorMessage() {
        UUID id = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateUserProfileUseCase.Param(id, "Flickit Admin", "bio", "link.com/admin"));
        assertThat(throwable).hasMessage("linkedin: " + UPDATE_USER_PROFILE_LINKEDIN_NOT_VALID);
    }

    @Test
    void testUpdateUserProfileParam_LinkedinUrlIsNull_Success() {
        UUID id = UUID.randomUUID();
        assertDoesNotThrow(
            () -> new UpdateUserProfileUseCase.Param(id, "Flickit Admin", "bio", null));
    }
}
