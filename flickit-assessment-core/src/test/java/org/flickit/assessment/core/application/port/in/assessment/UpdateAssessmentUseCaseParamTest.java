package org.flickit.assessment.core.application.port.in.assessment;

import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.apache.commons.lang3.RandomStringUtils.randomAlphabetic;
import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class UpdateAssessmentUseCaseParamTest {

    @Test
    void testUpdateAssessmentParam_shortTitleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.shortTitle(randomAlphabetic(21))));
        assertThat(throwable).hasMessage("shortTitle: " + UPDATE_ASSESSMENT_SHORT_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.shortTitle(" ab   ")));
        assertThat(throwable).hasMessage("shortTitle: " + UPDATE_ASSESSMENT_SHORT_TITLE_SIZE_MIN);
    }

    @Test
    void testUpdateAssessmentParam_titleParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title(randomAlphabetic(101))));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MAX);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("  ab  ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_SIZE_MIN);

        throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.title("   ")));
        assertThat(throwable).hasMessage("title: " + UPDATE_ASSESSMENT_TITLE_NOT_BLANK);
    }

    @Test
    void testUpdateAssessmentParam_langParamViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.lang("FR")));
        assertThat(throwable).hasMessage("lang: " + UPDATE_ASSESSMENT_LANGUAGE_INVALID);
    }

    @Test
    void testUpdateAssessmentUseCaseParam_langParamIsNull_Success() {
        assertDoesNotThrow(() -> createParam(b -> b.lang(null)));
    }

    @Test
    void testUpdateAssessmentParam_currentUserIdViolatesConstraints_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<UpdateAssessmentUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private UpdateAssessmentUseCase.Param.ParamBuilder paramBuilder() {
        return UpdateAssessmentUseCase.Param.builder()
            .id(UUID.randomUUID())
            .title("title")
            .shortTitle("shortTitle")
            .lang("FA")
            .currentUserId(UUID.randomUUID());
    }
}
