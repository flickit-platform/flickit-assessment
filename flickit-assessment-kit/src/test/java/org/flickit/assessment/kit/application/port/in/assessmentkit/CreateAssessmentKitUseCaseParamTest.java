package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;

class CreateAssessmentKitUseCaseParamTest {

    private static final String TITLE = "Title";
    private static final String SUMMARY = "Summary";
    private static final String ABOUT = "About";
    private static final boolean IS_PRIVATE = Boolean.FALSE;
    private static final Long EXPERT_GROUP_ID = 1L;
    private static final UUID CURRENT_USER_ID = UUID.randomUUID();

    @Test
    void testCreateAssessmentKit_titleIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(null, SUMMARY, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_KIT_TITLE_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKit_titleIsLessThanMin_ErrorMessage() {
        var title = "  ab  ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(title, SUMMARY, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_KIT_TITLE_SIZE_MIN);
    }

    @Test
    void testCreateAssessmentKit_titleIsMoreThanMax_ErrorMessage() {
        var title = RandomStringUtils.randomAlphanumeric(101);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(title, SUMMARY, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("title: " + CREATE_ASSESSMENT_KIT_TITLE_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentKit_SummaryIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, null, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("summary: " + CREATE_ASSESSMENT_KIT_SUMMARY_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKit_summaryIsLessThanMin_ErrorMessage() {
        var summary = "   ab  ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, summary, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("summary: " + CREATE_ASSESSMENT_KIT_SUMMARY_SIZE_MIN);
    }

    @Test
    void testCreateAssessmentKit_summaryIsMoreThanMax_ErrorMessage() {
        var summary = RandomStringUtils.randomAlphanumeric(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, summary, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("summary: " + CREATE_ASSESSMENT_KIT_SUMMARY_SIZE_MAX);
    }


    @Test
    void testCreateAssessmentKit_aboutIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, SUMMARY, null, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("about: " + CREATE_ASSESSMENT_KIT_ABOUT_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKit_aboutIsLessThanMin_ErrorMessage() {
        var about = "   ab  ";
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, SUMMARY, about, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("about: " + CREATE_ASSESSMENT_KIT_ABOUT_SIZE_MIN);
    }

    @Test
    void testCreateAssessmentKit_aboutIsMoreThanMax_ErrorMessage() {
        var about = RandomStringUtils.randomAlphanumeric(1001);
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, SUMMARY, about, IS_PRIVATE, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("about: " + CREATE_ASSESSMENT_KIT_ABOUT_SIZE_MAX);
    }

    @Test
    void testCreateAssessmentKit_isPrivateIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, SUMMARY, ABOUT, null, EXPERT_GROUP_ID, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("isPrivate: " + CREATE_ASSESSMENT_KIT_IS_PRIVATE_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKit_expertGroupIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, SUMMARY, ABOUT, IS_PRIVATE, null, CURRENT_USER_ID));
        assertThat(throwable).hasMessage("expertGroupId: " + CREATE_ASSESSMENT_KIT_EXPERT_GROUP_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessmentKit_currentUserIdIsNull_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessmentKitUseCase.Param(TITLE, SUMMARY, ABOUT, IS_PRIVATE, EXPERT_GROUP_ID, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }
}
