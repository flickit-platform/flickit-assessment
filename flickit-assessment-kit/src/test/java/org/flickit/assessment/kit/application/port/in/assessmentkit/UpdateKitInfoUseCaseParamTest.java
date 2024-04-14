package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitInfoUseCase.Param;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class UpdateKitInfoUseCaseParamTest {

    @Test
    void testUpdateKitInfo_kitIdIsNull_ErrorMessage() {
        String title = "title";
        String summary = "SUMMARY";
        String about = "about";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, title, summary, null, null, null, about, null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + EDIT_KIT_INFO_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitInfo_TitleIsLessThanLimit_ErrorMessage() {
        Long kitId = 1L;
        String summary = "SUMMARY";
        String minTitle = "t";
        String about = "about";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, minTitle, summary, null, null, null, about, null, currentUserId));
        assertThat(throwable).hasMessage("title: " + EDIT_KIT_INFO_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_TitleIsMoreThanLimit_ErrorMessage() {
        Long kitId = 1L;
        String summary = "SUMMARY";
        String maxTitle = RandomStringUtils.randomAlphabetic(51);
        String about = "about";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, maxTitle, summary, null, null, null, about, null, currentUserId));
        assertThat(throwable).hasMessage("title: " + EDIT_KIT_INFO_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_SummaryIsLessThanLimit_ErrorMessage() {
        Long kitId = 1L;
        String title = "title";
        String minSummary = "s";
        String about = "about";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, title, minSummary, null, null, null, about, null, currentUserId));
        assertThat(throwable).hasMessage("summary: " + EDIT_KIT_INFO_SUMMARY_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_SummaryIsMoreThanLimit_ErrorMessage() {
        Long kitId = 1L;
        String title = "title";
        String maxSummary = RandomStringUtils.randomAlphabetic(201);
        String about = "about";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, title, maxSummary, null, null, null, about, null, currentUserId));
        assertThat(throwable).hasMessage("summary: " + EDIT_KIT_INFO_SUMMARY_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_AboutIsLessThanLimit_ErrorMessage() {
        Long kitId = 1L;
        String title = "title";
        String summary = "SUMMARY";
        String minAbout = "a";
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, title, summary, null, null, null, minAbout, null, currentUserId));
        assertThat(throwable).hasMessage("about: " + EDIT_KIT_INFO_ABOUT_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_AboutIsMoreThanLimit_ErrorMessage() {
        Long kitId = 1L;
        String title = "title";
        String summary = "SUMMARY";
        String maxAbout = RandomStringUtils.randomAlphabetic(1001);
        UUID currentUserId = UUID.randomUUID();

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, title, summary, null, null, null, maxAbout, null, currentUserId));
        assertThat(throwable).hasMessage("about: " + EDIT_KIT_INFO_ABOUT_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_CurrentUserIdIsNull_ErrorMessage() {
        Long kitId = 1L;
        String title = "title";
        String summary = "SUMMARY";
        String about = "about";

        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(kitId, title, summary, null, null, null, about, null, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
