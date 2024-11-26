package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.ConstraintViolationException;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.Test;

import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAssessorAdviceNarrationUseCaseParamTest {

/*    @Test
    void testCreateAssessorAdviceNarrationParam_assessmentIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAssessorAdviceNarrationParam_assessorNarration_OptionalWithSizeLimit() {
        var currentUserId = UUID.randomUUID();
        var assessmentId = UUID.randomUUID();
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b->b.assessorNarration("na")));
        assertThat(throwable).hasMessage("assessorNarration: " + CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MIN);

        String assessorNarration = RandomStringUtils.randomAlphabetic(1501);
        throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAssessorAdviceNarrationUseCase.Param(assessmentId, assessorNarration,currentUserId));
        assertThat(throwable).hasMessage("assessorNarration: " + CREATE_ASSESSOR_ADVICE_NARRATION_ASSESSOR_NARRATION_SIZE_MAX);

        assertDoesNotThrow(() -> createParam(b->b.assessorNarration("")));
        assertDoesNotThrow(() -> createParam(b->b.assessorNarration("         ")));
    }

    @Test
    void testCreateAssessorAdviceNarrationParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b->b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private CreateAssessorAdviceNarrationUseCase.Param createParam(Consumer<CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private CreateAssessorAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAssessorAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .assessorNarration("narration")
            .currentUserId(UUID.randomUUID());
    }*/
}
