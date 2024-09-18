package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceAiNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceAiNarrationPromptPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother;
import org.flickit.assessment.common.application.MessageBundle;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.CallAiPromptPort;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.config.AppAiProperties;
import org.flickit.assessment.common.config.OpenAiProperties;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ai.chat.prompt.Prompt;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_AI_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.advice.common.MessageKey.ADVICE_NARRATION_AI_IS_DISABLED;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdviceAiNarrationServiceTest {

    @InjectMocks
    CreateAdviceAiAiNarrationService service;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    OpenAiProperties openAiProperties;

    @Mock
    CallAiPromptPort callAiPromptPort;

    @Mock
    LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Mock
    AppAiProperties appAiProperties;

    @Mock
    CreateAdviceAiNarrationPromptPort createAdviceAiNarrationPromptPort;

    @Test
    void testCreateAdviceAiNarration_UserDoesNotHaveRequiredPermission_ShouldThrowAccessDeniedException() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAdviceAiNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.createAdviceAiNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(appAiProperties,
            loadAssessmentResultPort,
            validateAssessmentResultPort,
            openAiProperties,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort);
    }

    @Test
    void testCreateAdviceAiNarration_AiIsDisabled_ShouldReturnAiIsDisabledMessage() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAdviceAiNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(false);

        var result = assertDoesNotThrow(() -> service.createAdviceAiNarration(param));
        assertEquals(MessageBundle.message(ADVICE_NARRATION_AI_IS_DISABLED), result.content());

        verifyNoInteractions(loadAssessmentResultPort,
            validateAssessmentResultPort,
            openAiProperties,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort);
    }

    @Test
    void testCreateAdviceAiNarration_AssessmentResultDoesNotNotExist_ShouldReturnResourceNoFound() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAdviceAiNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAdviceAiNarration(param));
        assertEquals(CREATE_ADVICE_AI_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(validateAssessmentResultPort,
            openAiProperties,
            callAiPromptPort,
            loadAdviceNarrationPort,
            createAdviceNarrationPort);
    }

    @Test
    void testCreateAdviceAiNarration_AdviceNarrationDoesNotExist_ShouldCreateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID());
        var param = new CreateAdviceAiNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var prompt = new Prompt("AI prompt");

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(appAiProperties.isEnabled()).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAdviceAiNarrationPromptPort.createAdviceAiNarrationPrompt(adviceListItems.toString(), attributeLevelTargets.toString())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiNarration);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));

        service.createAdviceAiNarration(param);

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testCreateAdviceAiNarration_AdviceNarrationExists_ShouldUpdateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID());
        var param = new CreateAdviceAiNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());
        var prompt = new Prompt("AI prompt");

        when(appAiProperties.isEnabled()).thenReturn(true);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(createAdviceAiNarrationPromptPort.createAdviceAiNarrationPrompt(adviceListItems.toString(), attributeLevelTargets.toString())).thenReturn(prompt);
        when(callAiPromptPort.call(prompt)).thenReturn(aiNarration);
        doNothing().when(updateAdviceNarrationPort).updateAiNarration(any(AdviceNarration.class));

         service.createAdviceAiNarration(param);

        verifyNoInteractions(createAdviceNarrationPort);
    }
}
