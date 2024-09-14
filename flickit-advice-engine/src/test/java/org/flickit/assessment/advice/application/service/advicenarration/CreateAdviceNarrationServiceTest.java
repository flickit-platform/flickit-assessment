package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AdviceNarration;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.port.in.advicenarration.CreateAdviceNarrationUseCase;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceAiNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.CreateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.UpdateAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AttributeLevelTargetMother;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdviceNarrationServiceTest {

    @InjectMocks
    CreateAdviceNarrationService createAdviceNarrationService;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    CreateAdviceAiNarrationPort createAdviceAiNarrationPort;

    @Mock
    LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    CreateAdviceNarrationPort createAdviceNarrationPort;

    @Mock
    UpdateAdviceNarrationPort updateAdviceNarrationPort;

    @Test
    void testCreateAdviceNarration_UserHasNoAccess_ShouldReturnAccessDenied() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ADVICE)).thenReturn(false);
        var throwable = assertThrows(AccessDeniedException.class, () -> createAdviceNarrationService.createAdviceNarration(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }

    @Test
    void testCreateAdviceNarration_AdviceNarrationDoesNotExist_ShouldUpdateWithNewAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var param = new CreateAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());
        var throwable = assertThrows(ResourceNotFoundException.class, () -> createAdviceNarrationService.createAdviceNarration(param));
        assertEquals(CREATE_ADVICE_NARRATION_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());
    }

    @Test
    void testCreateAdviceNarration_AdviceNarrationDoesNotExist_ShouldCreateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID());
        var param = new CreateAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.empty());
        when(createAdviceAiNarrationPort.createAdviceAiNarration(adviceListItems.toString(), attributeLevelTargets.toString())).thenReturn(aiNarration);
        doNothing().when(createAdviceNarrationPort).persist(any(AdviceNarration.class));
        assertDoesNotThrow(() -> createAdviceNarrationService.createAdviceNarration(param));

        verifyNoInteractions(updateAdviceNarrationPort);
    }

    @Test
    void testCreateAdviceNarration_AdviceNarrationExists_ShouldUpdateAdviceNarration() {
        var assessmentId = UUID.randomUUID();
        var adviceListItems = List.of(AdviceListItemMother.createSimpleAdviceListItem());
        var attributeLevelTargets = List.of(AttributeLevelTargetMother.createAttributeLevelTarget());
        var currentUserId = UUID.randomUUID();
        var assessmentResult = new AssessmentResult(UUID.randomUUID());
        var param = new CreateAdviceNarrationUseCase.Param(assessmentId, adviceListItems, attributeLevelTargets, currentUserId);
        var aiNarration = "aiNarration";
        var adviceNarration = new AdviceNarration(UUID.randomUUID(), assessmentResult.getId(), aiNarration, null, LocalDateTime.now(), null, UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.CREATE_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceNarrationPort.loadByAssessmentResultId(assessmentResult.getId())).thenReturn(Optional.of(adviceNarration));
        when(createAdviceAiNarrationPort.createAdviceAiNarration(adviceListItems.toString(), attributeLevelTargets.toString())).thenReturn(aiNarration);
        doNothing().when(updateAdviceNarrationPort).updateAiNarration(any(AdviceNarration.class));
        assertDoesNotThrow(() -> createAdviceNarrationService.createAdviceNarration(param));

        verifyNoInteractions(createAdviceNarrationPort);
    }
}
