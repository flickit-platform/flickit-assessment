package org.flickit.assessment.advice.application.port.in.advicenarration;

import jakarta.validation.ConstraintViolationException;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CreateAiAdviceNarrationUseCaseParamTest {

/*    @Test
    void testCreateAiAdviceNarrationParam_assessmentIdIsNull_ErrorMessage() {
        var currentUserId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAiAdviceNarrationUseCase.Param(null, adviceListItems, attributeLevelTargets, currentUserId));
        assertThat(throwable).hasMessage("assessmentId: " + CREATE_AI_ADVICE_NARRATION_ASSESSMENT_ID_NOT_NULL);
    }

    @Test
    void testCreateAiAdviceNarrationParam_adviceAiListItemsIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAiAdviceNarrationUseCase.Param(assessmentId, null, attributeLevelTargets, currentUserId));
        assertThat(throwable).hasMessage("adviceListItems: " + CREATE_AI_ADVICE_NARRATION_ADVICE_LIST_ITEMS_NOT_NULL);
    }

    @Test
    void testCreateAiAdviceNarrationParam_attributeLevelTargetsIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, null, currentUserId));
        assertThat(throwable).hasMessage("attributeLevelTargets: " + CREATE_AI_ADVICE_NARRATION_ATTRIBUTE_LEVEL_TARGETS_NOT_NULL);
    }

    @Test
    void testCreateAiAdviceNarrationParam_currentUserIdIsNull_ErrorMessage() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var throwable = assertThrows(ConstraintViolationException.class,
            () -> new CreateAiAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, null));
        assertThat(throwable).hasMessage("currentUserId: " + COMMON_CURRENT_USER_ID_NOT_NULL);
    }*/
}
