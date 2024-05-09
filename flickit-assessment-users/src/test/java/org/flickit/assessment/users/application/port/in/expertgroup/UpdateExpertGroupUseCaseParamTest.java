package org.flickit.assessment.users.application.port.in.expertgroup;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.users.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateExpertGroupUseCaseParamTest {

    @Test
    void testUpdateExpertGroup_idIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(null, "title", "bio", "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("id: " + UPDATE_EXPERT_GROUP_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testUpdateExpertGroup_titleIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, null, "bio", "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_EXPERT_GROUP_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateExpertGroup_titleIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "a", "bio", "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_EXPERT_GROUP_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateExpertGroup_titleIsGreaterThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String title = RandomStringUtils.randomAlphabetic(101);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, title, "bio", "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_EXPERT_GROUP_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateExpertGroup_bioIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", null, "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("bio: " + UPDATE_EXPERT_GROUP_BIO_NOT_BLANK);
    }

    @Test
    void testUpdateExpertGroup_bioIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "b", "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("bio: " + UPDATE_EXPERT_GROUP_BIO_SIZE_MIN);
    }

    @Test
    void testUpdateExpertGroup_bioIsGreaterThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String bio = RandomStringUtils.randomAlphabetic(201);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", bio, "about",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("bio: " + UPDATE_EXPERT_GROUP_BIO_SIZE_MAX);
    }

    @Test
    void testUpdateExpertGroup_aboutIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", null,
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("about: " + UPDATE_EXPERT_GROUP_ABOUT_NOT_BLANK);
    }

    @Test
    void testUpdateExpertGroup_aboutIsLessThanMin_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", "a",
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("about: " + UPDATE_EXPERT_GROUP_ABOUT_SIZE_MIN);
    }

    @Test
    void testUpdateExpertGroup_aboutIsGreaterThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String about = RandomStringUtils.randomAlphabetic(501);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", about,
                "https://www.google.com", currentUserId));
        assertThat(throwable).hasMessage("about: " + UPDATE_EXPERT_GROUP_ABOUT_SIZE_MAX);
    }

    @Test
    void testUpdateExpertGroup_websiteIsNull_Successful() {
        UUID currentUserId = UUID.randomUUID();
        assertDoesNotThrow(() -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", "about",
                null , currentUserId));
    }

    @Test
    void testUpdateExpertGroup_websiteIsNotUrl_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", "about",
                "a.c", currentUserId));
        assertThat(throwable).hasMessage("website: " + UPDATE_EXPERT_GROUP_WEBSITE_NOT_URL);
    }

    @Test
    void testUpdateExpertGroup_websiteIsGreaterThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        String website = "https://"+ RandomStringUtils.randomAlphabetic(201) + ".com";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", "about",
                website, currentUserId));
        assertThat(throwable).hasMessage("website: " + UPDATE_EXPERT_GROUP_WEBSITE_SIZE_MAX);
    }

    @Test
    void testUpdateExpertGroup_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new UpdateExpertGroupUseCase.Param(0L, "title", "bio", "about",
                "https://www.google.com", null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
