package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static org.flickit.assessment.advice.test.fixture.application.AssessmentResultMother.createAssessmentResultWithAssessmentId;
import static org.flickit.assessment.advice.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.REFRESH_ASSESSMENT_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RefreshAssessmentAdviceServiceTest {

    @InjectMocks
    RefreshAssessmentAdviceService service;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    LoadMaturityLevelsPort loadMaturityLevelsPort;

    @Mock
    LoadAttributesPort loadAttributesPort;

    @Mock
    CreateAdviceHelper createAdviceHelper;

    @Mock
    CreateAiAdviceNarrationHelper createAiAdviceNarrationHelper;

    private RefreshAssessmentAdviceUseCase.Param param = createParam(RefreshAssessmentAdviceUseCase.Param.ParamBuilder::build);
    private AssessmentResult assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());

    private LoadAttributesPort.Result attributeResult = new LoadAttributesPort.Result(123L, new LoadAttributesPort.MaturityLevel(levelOne().getId(), levelOne().getTitle(), "Low", 1, 2));
    private List<LoadAttributesPort.Result> attributes = List.of(attributeResult);

    private final AdviceListItem adviceListItem = AdviceListItemMother.createSimpleAdviceListItem();
    private final List<AdviceListItem> adviceListItems = List.of(adviceListItem);

    @Test
    void testRefreshAssessmentAdvice_whenUserNotAuthorized_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.refreshAssessmentAdvice(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadMaturityLevelsPort,
            loadAttributesPort,
            createAdviceHelper);
    }

    @Test
    void testRefreshAssessmentAdvice_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.refreshAssessmentAdvice(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort,
            loadAttributesPort,
            createAdviceHelper);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalse_thenShouldNotCreateAndSaveAdvice() {
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributesPort.loadAll(param.getAssessmentId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage())).thenReturn(attributes);

        service.refreshAssessmentAdvice(param);

        verifyNoInteractions(createAdviceHelper, createAiAdviceNarrationHelper);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsTrue_thenCreateAndSaveAdvice() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributesPort.loadAll(param.getAssessmentId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage())).thenReturn(attributes);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(createAdviceHelper.createAdvice(eq(param.getAssessmentId()), anyList())).thenReturn(adviceListItems);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(createAdviceHelper).createAdvice(eq(param.getAssessmentId()), targetCaptor.capture());
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(adviceListItems),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(1, capturedTargets.size());
        assertEquals(123L, capturedTargets.getFirst().getAttributeId());
        assertEquals(levelTwo().getId(), capturedTargets.getFirst().getMaturityLevelId());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(1, narratedTargets.size());
        assertEquals(123L, narratedTargets.getFirst().getAttributeId());
        assertEquals(levelTwo().getId(), narratedTargets.getFirst().getMaturityLevelId());
    }

    @Test
    void testRefreshAssessmentAdvice_whenAttributeHasMaxMaturityLevel_thenNoTargetGenerated() {
        attributeResult = new LoadAttributesPort.Result(123L, new LoadAttributesPort.MaturityLevel(levelThree().getId(), levelOne().getTitle(), "High", levelThree().getIndex(), 2));
        attributes = List.of(attributeResult);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributesPort.loadAll(param.getAssessmentId(), assessmentResult.getKitVersionId(), assessmentResult.getLanguage())).thenReturn(attributes);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(createAdviceHelper.createAdvice(eq(param.getAssessmentId()), anyList())).thenReturn(adviceListItems);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(createAdviceHelper).createAdvice(eq(param.getAssessmentId()), targetCaptor.capture());
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(adviceListItems),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(0, capturedTargets.size());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(0, narratedTargets.size());

        verify(createAdviceHelper).createAdvice(eq(param.getAssessmentId()), eq(List.of()));
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(eq(assessmentResult), eq(adviceListItems), eq(List.of()));
    }

    private RefreshAssessmentAdviceUseCase.Param createParam(Consumer<RefreshAssessmentAdviceUseCase.Param.ParamBuilder> changer) {
        var paramBuilder = paramBuilder();
        changer.accept(paramBuilder);
        return paramBuilder.build();
    }

    private RefreshAssessmentAdviceUseCase.Param.ParamBuilder paramBuilder() {
        return RefreshAssessmentAdviceUseCase.Param.builder()
            .assessmentId(UUID.randomUUID())
            .forceRegenerate(true)
            .currentUserId(UUID.randomUUID());
    }
}
