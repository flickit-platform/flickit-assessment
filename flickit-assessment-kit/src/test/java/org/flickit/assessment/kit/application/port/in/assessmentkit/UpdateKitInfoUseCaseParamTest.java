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

    private static final Long KIT_ID = 1L;
    private static final String TITLE = "title";
    private static final String MIN_TITLE = "t";
    private static final String MAX_TITLE = RandomStringUtils.randomAlphabetic(51);
    private static final String SUMMARY = "summary";
    private static final String MIN_SUMMARY = "s";
    private static final String MAX_SUMMARY = RandomStringUtils.randomAlphabetic(201);
    private static final Boolean IS_ACTIVE = Boolean.TRUE;
    private static final Boolean IS_PRIVATE = Boolean.FALSE;
    private static final Double PRICE = 0D;
    private static final String ABOUT = "about";
    private static final String MIN_ABOUT = "a";
    private static final String MAX_ABOUT = RandomStringUtils.randomAlphabetic(1001);
    private static final List<Long> TAGS = List.of(2L, 3L);
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testUpdateKitInfo_kitIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(null, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("kitId: " + EDIT_KIT_INFO_KIT_ID_NOT_NULL);
    }

    @Test
    void testUpdateKitInfo_TitleIsLessThanLimit_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, MIN_TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("title: " + EDIT_KIT_INFO_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_TitleIsMoreThanLimit_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, MAX_TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("title: " + EDIT_KIT_INFO_TITLE_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_SummaryIsLessThanLimit_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, TITLE, MIN_SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("summary: " + EDIT_KIT_INFO_SUMMARY_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_SummaryIsMoreThanLimit_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, TITLE, MAX_SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("summary: " + EDIT_KIT_INFO_SUMMARY_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_AboutIsLessThanLimit_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, MIN_ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("about: " + EDIT_KIT_INFO_ABOUT_SIZE_MIN);
    }

    @Test
    void testUpdateKitInfo_AboutIsMoreThanLimit_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, MAX_ABOUT, TAGS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("about: " + EDIT_KIT_INFO_ABOUT_SIZE_MAX);
    }

    @Test
    void testUpdateKitInfo_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new Param(KIT_ID, TITLE, SUMMARY, IS_ACTIVE, IS_PRIVATE, PRICE, ABOUT, TAGS, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
