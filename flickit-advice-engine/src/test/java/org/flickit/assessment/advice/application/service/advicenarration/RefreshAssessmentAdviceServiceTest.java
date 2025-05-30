package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.MaturityLevel;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
import org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother;
import org.flickit.assessment.advice.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
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

    private final AssessmentResult assessmentResult = AssessmentResultMother.createAssessmentResult();
    private RefreshAssessmentAdviceUseCase.Param param = createParam(RefreshAssessmentAdviceUseCase.Param.ParamBuilder::build);

    @Test
    void testRefreshAssessmentAdvice_whenUserNotAuthorized_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.REFRESH_ASSESSMENT_ADVICE))
            .thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.refreshAssessmentAdvice(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAssessmentResultPort,
            loadMaturityLevelsPort,
            loadAttributesPort,
            createAdviceHelper);
    }

    @Test
    void testRefreshAssessmentAdvice_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.REFRESH_ASSESSMENT_ADVICE))
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
    void testRefreshAssessmentAdvice_whenForceRegenerateIsTrue_thenCreateAndSaveAdvice() {
        MaturityLevel level1 = new MaturityLevel(1L, "Low", 1);
        MaturityLevel level2 = new MaturityLevel(3L, "Medium", 2);
        MaturityLevel level3 = new MaturityLevel(2L, "High", 3);
        List<MaturityLevel> maturityLevels = List.of(level2, level3, level1);

        LoadAttributesPort.Result attributeResult1 = new LoadAttributesPort.Result(123L, new LoadAttributesPort.MaturityLevel(1L, "Low", "Low", 1, 2));
        LoadAttributesPort.Result attributeResult2 = new LoadAttributesPort.Result(123L, new LoadAttributesPort.MaturityLevel(2L, "High", "High", 3, 2));
        List<LoadAttributesPort.Result> attributes = List.of(attributeResult1, attributeResult2);

        AdviceListItem adviceListItem = AdviceListItemMother.createSimpleAdviceListItem();
        List<AdviceListItem> adviceListItems = List.of(adviceListItem);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(attributes);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(createAdviceHelper.createAdvice(eq(assessmentResult.getAssessmentId()), targetCaptor.capture())).thenReturn(adviceListItems);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(adviceListItems),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(1, capturedTargets.size());
        assertEquals(123L, capturedTargets.getFirst().getAttributeId());
        assertEquals(3L, capturedTargets.getFirst().getMaturityLevelId());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(1, narratedTargets.size());
        assertEquals(123L, narratedTargets.getFirst().getAttributeId());
        assertEquals(3L, narratedTargets.getFirst().getMaturityLevelId());
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalse_thenShouldNotCreateAndSaveAdvice() {
        param = createParam(b -> b.forceRegenerate(false));
        MaturityLevel level1 = new MaturityLevel(1L, "Low", 1);
        MaturityLevel level2 = new MaturityLevel(3L, "Medium", 2);
        MaturityLevel level3 = new MaturityLevel(2L, "High", 3);
        List<MaturityLevel> maturityLevels = List.of(level2, level3, level1);

        LoadAttributesPort.Result attributeResult = new LoadAttributesPort.Result(123L, new LoadAttributesPort.MaturityLevel(1L, "Low", "Low", 1, 2));
        List<LoadAttributesPort.Result> attributes = List.of(attributeResult);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(attributes);

        service.refreshAssessmentAdvice(param);

        verifyNoInteractions(createAdviceHelper, createAiAdviceNarrationHelper);
    }

    @Test
    void testRefreshAssessmentAdvice_whenAdviceNarrationDoesNotExists_thenShouldMakeAdviceNarrationAndAdviceItem() {
        MaturityLevel level1 = new MaturityLevel(1L, "Low", 1);
        MaturityLevel level2 = new MaturityLevel(3L, "Medium", 2);
        MaturityLevel level3 = new MaturityLevel(2L, "High", 3);
        List<MaturityLevel> maturityLevels = List.of(level2, level3, level1);

        LoadAttributesPort.Result attributeResult = new LoadAttributesPort.Result(123L, new LoadAttributesPort.MaturityLevel(1L, "Low", "Low", 1, 2));
        List<LoadAttributesPort.Result> attributes = List.of(attributeResult);

        AdviceListItem adviceListItem = AdviceListItemMother.createSimpleAdviceListItem();
        List<AdviceListItem> adviceListItems = List.of(adviceListItem);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), AssessmentPermission.REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(maturityLevels);
        when(loadAttributesPort.loadAll(param.getAssessmentId())).thenReturn(attributes);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(createAdviceHelper.createAdvice(eq(assessmentResult.getAssessmentId()), targetCaptor.capture())).thenReturn(adviceListItems);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(adviceListItems),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(1, capturedTargets.size());
        assertEquals(123L, capturedTargets.getFirst().getAttributeId());
        assertEquals(3L, capturedTargets.getFirst().getMaturityLevelId());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(1, narratedTargets.size());
        assertEquals(123L, narratedTargets.getFirst().getAttributeId());
        assertEquals(3L, narratedTargets.getFirst().getMaturityLevelId());
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
