package org.flickit.assessment.core.application.port.in.advicenarration;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.core.test.fixture.application.AdvicePlanItemMother;
import org.flickit.assessment.core.test.fixture.application.AttributeLevelTargetMother;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAiAdviceNarrationUseCaseParamTest {

    @Test
    void testCreateAiAdviceNarrationParam_assessmentIdIsParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.assessmentId(null)));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_AI_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAiAdviceNarrationParam_adviceAiListItemsParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.adviceListItems(null)));
        assertThat(throwable).hasMessage("adviceListItems: " + CREATE_AI_ADVICE_NARRATION_ADVICE_LIST_ITEMS_NOT_NULL);
    }

    @Test
    void testCreateAiAdviceNarrationParam_attributeLevelTargetsParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.attributeLevelTargets(null)));
        assertThat(throwable).hasMessage("attributeLevelTargets: " + CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL);
    }

    @Test
    void testCreateAiAdviceNarrationParam_currentUserIdParamViolatesConstraint_ErrorMessage() {
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> createParam(b -> b.currentUserId(null)));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }

    private void createParam(Consumer<CreateAiAdviceNarrationUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        paramBuilder.build();
    }

    private CreateAiAdviceNarrationUseCase.Param.ParamBuilder paramBuilder() {
        return CreateAiAdviceNarrationUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .adviceListItems(List.of(AdvicePlanItemMother.createSimpleAdvicePlanItem()))
            .attributeLevelTargets(List.of(AttributeLevelTargetMother.createAttributeLevelTarget()))
            .currentUserId(UUID.randomUUID());
    }
}
