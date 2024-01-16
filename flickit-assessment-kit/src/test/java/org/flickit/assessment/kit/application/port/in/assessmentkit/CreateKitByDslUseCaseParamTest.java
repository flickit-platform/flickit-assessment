package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
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
class CreateKitByDslUseCaseParamTest {

    private static final Long KIT_DSL_ID = 1L;
    private static final boolean IS_PRIVATE = Boolean.FALSE;
    private static final Long EXPERT_GROUP_ID = 1L;
    private static final String TITLE = "Title";
    private static final String SUMMARY = "Summary";
    private static final String ABOUT = "About";
    private static final List<Long> TAG_IDS = List.of(1L, 2L);
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testCreateKitByDsl_kitDslIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, IS_PRIVATE, null, EXPERT_GROUP_ID, TAG_IDS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("kitDslId: " + CREATE_KIT_BY_DSL_KIT_DSL_JSON_ID_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_isPrivateIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, null, KIT_DSL_ID, EXPERT_GROUP_ID, TAG_IDS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("isPrivate: " + CREATE_KIT_BY_DSL_IS_PRIVATE_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_expertGroupIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, IS_PRIVATE, KIT_DSL_ID, null, TAG_IDS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("expertGroupId: " + CREATE_KIT_BY_DSL_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_titleIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(null, SUMMARY, ABOUT, IS_PRIVATE, KIT_DSL_ID, EXPERT_GROUP_ID, TAG_IDS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("title: " + CREATE_KIT_BY_DSL_TITLE_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_summaryIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, null, ABOUT, IS_PRIVATE, KIT_DSL_ID, EXPERT_GROUP_ID, TAG_IDS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("summary: " + CREATE_KIT_BY_DSL_SUMMARY_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_aboutIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, SUMMARY, null, IS_PRIVATE, KIT_DSL_ID, EXPERT_GROUP_ID, TAG_IDS, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("about: " + CREATE_KIT_BY_DSL_ABOUT_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_tagIdsIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, IS_PRIVATE, KIT_DSL_ID, EXPERT_GROUP_ID, null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("tagIds: " + CREATE_KIT_BY_DSL_TAG_IDS_NOT_NULL);
    }

    @Test
    void testCreateKitByDsl_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateKitByDslUseCase.Param(TITLE, SUMMARY, ABOUT, IS_PRIVATE, KIT_DSL_ID, EXPERT_GROUP_ID, TAG_IDS, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
