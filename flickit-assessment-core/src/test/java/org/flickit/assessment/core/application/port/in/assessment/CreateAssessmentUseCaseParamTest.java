package org.flickit.assessment.core.application.port.in.assessment;


import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessmentUseCaseParamTest {

    @Test
    void tesCreateAssessmentUseCaseParam_TitleIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(0L, null, "shortTitle", 1L, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void tesCreateAssessmentUseCaseParam_TitleIsShort_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(0L, "hi", "shortTitle",1L, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_TITLE_SIZE_MIN);
    }

    @Test
    void tesCreateAssessmentUseCaseParam_TitleIsLong_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var title = RandomStringUtils.random(101, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(0L, title, "shortTitle", 1L, currentUserId));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_TITLE_SIZE_MAX);
    }

    @Test
    void tesCreateAssessmentUseCaseParam_ShortTitleIsGreaterThanMax_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var shortTitle = RandomStringUtils.random(21, true, true);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(0L, "title", shortTitle, 1L, currentUserId));
        assertThat(throwable).hasMessage("shortTitle: " + CREATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX);
    }

    @Test
    void tesCreateAssessmentUseCaseParam_SpaceIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(null, "title", "shortTitle", 1L, currentUserId));
        assertThat(throwable).hasMessage("spaceId: " + CREATE_ASSESSMENT_SPACE_ID_NOT_NULL);
    }

    @Test
    void tesCreateAssessmentUseCaseParam_KitIdIsNull_ErrorMessage() {
        UUID currentUserId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(0L, "title", "shortTitle", null, currentUserId));
        assertThat(throwable).hasMessage("kitId: " + CREATE_ASSESSMENT_ASSESSMENT_KIT_ID_NOT_NULL);
    }

    @Test
    void tesCreateAssessmentUseCaseParam_CurrentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentUseCase.Param(0L, "title", "shortTitle", 1L, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
