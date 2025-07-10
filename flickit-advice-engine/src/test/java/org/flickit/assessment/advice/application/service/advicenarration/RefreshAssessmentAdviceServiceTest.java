package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeValuesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.CreateAdviceHelper;
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

import static org.flickit.assessment.advice.test.fixture.application.AdviceListItemMother.createSimpleAdviceListItem;
import static org.flickit.assessment.advice.test.fixture.application.AssessmentResultMother.createAssessmentResultWithAssessmentId;
import static org.flickit.assessment.advice.test.fixture.application.AttributeMother.createWithWeight;
import static org.flickit.assessment.advice.test.fixture.application.MaturityLevelMother.*;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.REFRESH_ASSESSMENT_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_ASSESSMENT_RESULT_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
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
    LoadAttributeValuesPort loadAttributeValuesPort;

    @Mock
    CreateAdviceHelper createAdviceHelper;

    @Mock
    CreateAiAdviceNarrationHelper createAiAdviceNarrationHelper;

    @Mock
    DeleteAdviceItemPort deleteAdviceItemPort;

    @Mock
    LoadAdviceItemPort loadAdviceItemPort;

    @Mock
    LoadAdviceNarrationPort loadAdviceNarrationPort;

    @Mock
    LoadAttributesPort loadAttributesPort;

    private RefreshAssessmentAdviceUseCase.Param param = createParam(RefreshAssessmentAdviceUseCase.Param.ParamBuilder::build);
    private AssessmentResult assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());

    private final AdviceListItem adviceListItem = createSimpleAdviceListItem();
    private final List<AdviceListItem> adviceListItems = List.of(adviceListItem);
    private final Attribute attribute1 = createWithWeight(1), attribute2 = createWithWeight(3), attribute3 = createWithWeight(5);

    @Test
    void testRefreshAssessmentAdvice_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.refreshAssessmentAdvice(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAttributeValuesPort,
            loadMaturityLevelsPort,
            createAdviceHelper,
            createAiAdviceNarrationHelper,
            deleteAdviceItemPort,
            loadAdviceItemPort,
            loadAdviceNarrationPort,
            loadAttributesPort);
    }

    @Test
    void testRefreshAssessmentAdvice_whenAssessmentResultNotFound_thenThrowResourceNotFoundException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.empty());

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.refreshAssessmentAdvice(param));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadMaturityLevelsPort,
            loadAttributeValuesPort,
            createAdviceHelper,
            createAiAdviceNarrationHelper,
            deleteAdviceItemPort,
            loadAdviceItemPort,
            loadAdviceNarrationPort,
            loadAttributesPort);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalseAndAdviceExists_thenDoNotRegenerateAdvice() {
        param = createParam(b -> b.forceRegenerate(false));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(true);
        when(loadAdviceNarrationPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(true);

        service.refreshAssessmentAdvice(param);

        verifyNoInteractions(loadMaturityLevelsPort,
            loadAttributeValuesPort,
            createAdviceHelper,
            createAiAdviceNarrationHelper,
            deleteAdviceItemPort,
            loadAttributesPort);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsTrue_thenRegenerateAdvice() {
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelTwo().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelOne().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId()))).thenReturn(List.of(attribute1, attribute2, attribute3));
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
        assertEquals(2, capturedTargets.size());
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(2, narratedTargets.size());
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));

        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
        verifyNoInteractions(loadAdviceItemPort, loadAdviceNarrationPort);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalseAndAdviceItemsDoesNotExists_thenRegenerateAdvice() {
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelTwo().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelOne().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(false);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId()))).thenReturn(List.of(attribute1, attribute2, attribute3));
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
        assertEquals(2, capturedTargets.size());
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(2, narratedTargets.size());
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));


        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
        verifyNoInteractions(loadAdviceNarrationPort);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalseAndAdviceItemExistsAndAdviceNarrationDoesNotExists_thenRegenerateAdvice() {
        var attribute4 = createWithWeight(7);
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelTwo().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelOne().getId()),
            new LoadAttributeValuesPort.Result(attribute4.getId(), levelOne().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(true);
        when(loadAdviceNarrationPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(false);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId()))).thenReturn(List.of(attribute1, attribute2, attribute3, attribute4));
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
        assertEquals(2, capturedTargets.size());
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute4.getId())));

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(2, narratedTargets.size());
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute4.getId())));

        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
    }

    @Test
    void testRefreshAssessmentAdvice_whenAttributeHasMaxMaturityLevel_thenNoTargetGenerated() {
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(123L, levelFive().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId()))).thenReturn(List.of(attribute3));

        service.refreshAssessmentAdvice(param);

        verifyNoInteractions(deleteAdviceItemPort,
            createAdviceHelper,
            createAiAdviceNarrationHelper,
            loadAdviceItemPort,
            loadAdviceNarrationPort,
            loadAdviceItemPort,
            loadAdviceNarrationPort);
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
