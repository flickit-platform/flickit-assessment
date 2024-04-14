package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Param;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateKitInfoUseCaseParamTest {

    @Test
    void testUpdateKitInfo_kitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, "title", "summary", null, null, null, "about", null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + UPDATE_KIT_INFO_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitInfo_TitleLengthIsLessThanLimit_ErrorMessage() {
        String minTitle = "t";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, minTitle, "summary", null, null, null, "about", null, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_INFO_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_TitleLengthIsMoreThanLimit_ErrorMessage() {
        String maxTitle = RandomStringUtils.randomAlphabetic(51);
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, maxTitle, "summary", null, null, null, "about", null, currentUserId));
        assertThat(throwable).hasMessage("title: " + UPDATE_KIT_INFO_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_SummaryLengthIsLessThanLimit_ErrorMessage() {
        String minSummary = "s";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, "title", minSummary, null, null, null, "about", null, currentUserId));
        assertThat(throwable).hasMessage("summary: " + UPDATE_KIT_INFO_SUMMARY_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_SummaryLengthIsMoreThanLimit_ErrorMessage() {
        String maxSummary = RandomStringUtils.randomAlphabetic(201);
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, "title", maxSummary, null, null, null, "about", null, currentUserId));
        assertThat(throwable).hasMessage("summary: " + UPDATE_KIT_INFO_SUMMARY_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_AboutIsLessThanLimit_ErrorMessage() {
        String minAbout = "a";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, "title", "summary", null, null, null, minAbout, null, currentUserId));
        assertThat(throwable).hasMessage("about: " + UPDATE_KIT_INFO_ABOUT_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_AboutIsMoreThanLimit_ErrorMessage() {
        String maxAbout = RandomStringUtils.randomAlphabetic(1001);
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, "title", "summary", null, null, null, maxAbout, null, currentUserId));
        assertThat(throwable).hasMessage("about: " + UPDATE_KIT_INFO_ABOUT_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_tagsIsEmpty_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        List<Long> tags = List.of();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, null, null, null, null, null, null, tags, currentUserId));
        assertThat(throwable).hasMessage("tags: " + UPDATE_KIT_INFO_TAGS_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(123L, null, null, null, null, null, null, null, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
