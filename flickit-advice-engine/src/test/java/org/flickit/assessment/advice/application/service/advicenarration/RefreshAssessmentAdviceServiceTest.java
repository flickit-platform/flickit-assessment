package org.flickit.assessment.advice.application.service.advicenarration;

import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.QuestionRecommendation;
import org.flickit.assessment.advice.application.port.in.advicenarration.RefreshAssessmentAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.adviceitem.DeleteAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.adviceitem.LoadAdviceItemPort;
import org.flickit.assessment.advice.application.port.out.advicenarration.LoadAdviceNarrationPort;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.atribute.LoadAttributesPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeValuesPort;
import org.flickit.assessment.advice.application.port.out.maturitylevel.LoadMaturityLevelsPort;
import org.flickit.assessment.advice.application.service.advice.GenerateAdvicePlanHelper;
import org.flickit.assessment.advice.test.fixture.application.QuestionRecommendationMother;
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
import java.util.stream.IntStream;

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
    GenerateAdvicePlanHelper generateAdvicePlanHelper;

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

    private final List<QuestionRecommendation> questionRecommendations = createAdviceListItems(10);
    private final Attribute attribute1 = createWithWeight(1), attribute2 = createWithWeight(3), attribute3 = createWithWeight(5);

    @Test
    void testRefreshAssessmentAdvice_whenUserDoesNotHaveRequiredPermission_thenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.refreshAssessmentAdvice(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadAssessmentResultPort,
            loadAttributeValuesPort,
            loadMaturityLevelsPort,
            generateAdvicePlanHelper,
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
            generateAdvicePlanHelper,
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
            generateAdvicePlanHelper,
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
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(generateAdvicePlanHelper.createAdvice(eq(param.getAssessmentId()), anyList())).thenReturn(questionRecommendations);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(generateAdvicePlanHelper).createAdvice(eq(param.getAssessmentId()), targetCaptor.capture());
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(questionRecommendations),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(2, capturedTargets.size());
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));

        AttributeLevelTarget attribute1LevelTarget = capturedTargets.stream()
            .filter(t -> t.getAttributeId() == attribute1.getId()).findFirst().orElseThrow();
        assertEquals(levelThree().getId(), attribute1LevelTarget.getMaturityLevelId());
        AttributeLevelTarget attribute3LevelTarget = capturedTargets.stream()
            .filter(t -> t.getAttributeId() == attribute3.getId()).findFirst().orElseThrow();
        assertEquals(levelThree().getId(), attribute3LevelTarget.getMaturityLevelId());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(2, narratedTargets.size());
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));

        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
        verifyNoInteractions(loadAdviceItemPort, loadAdviceNarrationPort);
    }

    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalseAndAdviceItemsDoesNotExists_thenRegenerateAdvice() {
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelFive().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelOne().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(false);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId()))).thenReturn(List.of(attribute1, attribute2, attribute3));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(generateAdvicePlanHelper.createAdvice(eq(param.getAssessmentId()), anyList())).thenReturn(questionRecommendations);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(generateAdvicePlanHelper).createAdvice(eq(param.getAssessmentId()), targetCaptor.capture());
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(questionRecommendations),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(2, capturedTargets.size());
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));

        AttributeLevelTarget attribute2LevelTarget = capturedTargets.stream()
            .filter(t -> t.getAttributeId() == attribute2.getId()).findFirst().orElseThrow();
        assertEquals(levelFive().getId(), attribute2LevelTarget.getMaturityLevelId());
        AttributeLevelTarget attribute3LevelTarget = capturedTargets.stream()
            .filter(t -> t.getAttributeId() == attribute3.getId()).findFirst().orElseThrow();
        assertEquals(levelThree().getId(), attribute3LevelTarget.getMaturityLevelId());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(2, narratedTargets.size());
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));

        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
        verifyNoInteractions(loadAdviceNarrationPort);
    }

    /*
    | Attribute   | Weight | Maturity Level | Furthest Score |      Selected        | Iteration |
    |-------------|--------|----------------|----------------|----------------------|-----------|
    | attribute1  |   1    |       3        |       2        | no                   |     X     |
    | attribute2  |   3    |       3        |       6        | yes (furthest)       |     0     |
    | attribute3  |   5    |       5        |       x        | no                   |     X     |
    | attribute4  |   7    |       1        |       x        | yes (below median)   |     0     |
    | attribute5  |   8    |       4        |       8        | yes (furthest)       |     0     |
    | attribute6  |   1    |       4        |       1        | no                   |     X     |
    ---------------------------------------------------------------------------------------------
    |
    |Furthest Score is calculated as: weight multiplied by (maxLevel - currentLevel)
    */
    @Test
    void testRefreshAssessmentAdvice_whenForceRegenerateIsFalseAndAdviceItemExistsAndAdviceNarrationDoesNotExists_thenRegenerateAdvice() {
        Attribute attribute4 = createWithWeight(7), attribute5 = createWithWeight(8), attribute6 = createWithWeight(1);
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelFive().getId()),
            new LoadAttributeValuesPort.Result(attribute4.getId(), levelOne().getId()),
            new LoadAttributeValuesPort.Result(attribute5.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute6.getId(), levelFour().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(true);
        when(loadAdviceNarrationPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(false);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId()))).thenReturn(List.of(attribute1, attribute2, attribute3, attribute4, attribute5, attribute6));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> targetCaptor = ArgumentCaptor.forClass(List.class);
        when(generateAdvicePlanHelper.createAdvice(eq(param.getAssessmentId()), anyList())).thenReturn(questionRecommendations);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationCaptor = ArgumentCaptor.forClass(List.class);

        service.refreshAssessmentAdvice(param);
        verify(generateAdvicePlanHelper).createAdvice(eq(param.getAssessmentId()), targetCaptor.capture());
        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            eq(questionRecommendations),
            narrationCaptor.capture()
        );
        List<AttributeLevelTarget> capturedTargets = targetCaptor.getValue();
        assertEquals(3, capturedTargets.size());
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute4.getId())));
        assertTrue(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute5.getId())));
        assertFalse(capturedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute6.getId())));

        AttributeLevelTarget attribute2LevelTarget = capturedTargets.stream()
            .filter(t -> t.getAttributeId() == attribute2.getId()).findFirst().orElseThrow();
        assertEquals(levelFour().getId(), attribute2LevelTarget.getMaturityLevelId());
        AttributeLevelTarget attribute4LevelTarget = capturedTargets.stream()
            .filter(t -> t.getAttributeId() == attribute4.getId()).findFirst().orElseThrow();
        assertEquals(levelThree().getId(), attribute4LevelTarget.getMaturityLevelId());

        List<AttributeLevelTarget> narratedTargets = narrationCaptor.getValue();
        assertEquals(3, narratedTargets.size());
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute1.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute2.getId())));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute3.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute4.getId())));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute5.getId())));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == (attribute6.getId())));

        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
    }

    /*
    | Attribute         | Weight | Maturity Level | Furthest Score |      Selected        |     Iteration     |
    |-------------------|--------|----------------|----------------|----------------------|-------------------|
    | attribute1(1035)  |   1    |       4        |       1        | no                   |       X           |
    | attribute2(1036)  |   3    |       3        |       6        | yes (furthest)       |       0           |
    | attribute3(1037)  |   5    |       5        |       x        | no                   |       X           |
    | attribute4(1038)  |   7    |       1        |       x        | yes (below median)   |       0           |
    | attribute5(1039)  |   8    |       4        |       8        | yes (furthest)       |       0           |
    | attribute6(1040)  |   1    |       4        |       1        | no                   |       X           |
    | attribute7(1041)  |   2    |       4        |       2        | yes (few questions)  |       2           |
    | attribute8(1042)  |   4    |       4        |       4        | yes (few questions)  |       1           |
    -----------------------------------------------------------------------------------------------------------
    |
    |Furthest Score is calculated as: weight multiplied by (maxLevel - currentLevel)
    */
    @Test
    void testRefreshAssessmentAdvice_whenThereIsFewQuestions_thenRegenerateAdvice() {
        var adviceListItems1 = createAdviceListItems(4);
        var adviceListItems2 = createAdviceListItems(8);
        var adviceListItems3 = createAdviceListItems(11);
        Attribute attribute4 = createWithWeight(7), attribute5 = createWithWeight(8), attribute6 = createWithWeight(1),
            attribute7 = createWithWeight(2), attribute8 = createWithWeight(4);
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelFive().getId()),
            new LoadAttributeValuesPort.Result(attribute4.getId(), levelOne().getId()),
            new LoadAttributeValuesPort.Result(attribute5.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute6.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute7.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute8.getId(), levelFour().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(true);
        when(loadAdviceNarrationPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(false);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId())))
            .thenReturn(List.of(attribute1, attribute2, attribute3, attribute4, attribute5, attribute6, attribute7, attribute8));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> adviceTargetsCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QuestionRecommendation>> improvableCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationTargetsCaptor = ArgumentCaptor.forClass(List.class);

        when(generateAdvicePlanHelper.createAdvice(eq(param.getAssessmentId()), anyList()))
            .thenReturn(adviceListItems1)
            .thenReturn(adviceListItems2)
            .thenReturn(adviceListItems3);

        service.refreshAssessmentAdvice(param);

        verify(generateAdvicePlanHelper, times(3)).createAdvice(eq(param.getAssessmentId()), adviceTargetsCaptor.capture());

        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            improvableCaptor.capture(),
            narrationTargetsCaptor.capture()
        );

        List<List<AttributeLevelTarget>> allCalls = adviceTargetsCaptor.getAllValues();

        assertEquals(3, allCalls.get(0).size());
        assertEquals(4, allCalls.get(1).size());
        assertEquals(5, allCalls.get(2).size());

        assertTrue(allCalls.getFirst().stream().anyMatch(t -> t.getAttributeId() == attribute2.getId()));
        assertTrue(allCalls.getFirst().stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(allCalls.getFirst().stream().anyMatch(t -> t.getAttributeId() == attribute5.getId()));

        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute2.getId()));
        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute5.getId()));
        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute8.getId()));

        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute2.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute5.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute8.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute7.getId()));

        assertEquals(11, improvableCaptor.getValue().size());

        List<AttributeLevelTarget> narratedTargets = narrationTargetsCaptor.getValue();
        assertEquals(5, narratedTargets.size());
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute3.getId()));
        assertFalse(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute1.getId()));

        verify(deleteAdviceItemPort, times(1)).deleteAllAiGenerated(assessmentResult.getId());
    }

    /*
    | Attribute         | Weight | Maturity Level | Furthest Score |      Selected        | Iteration   |
    |-------------------|--------|----------------|----------------|----------------------|-------------|
    | attribute1(1035)  |   1    |       3        |       2        | no                   |      X      |
    | attribute2(1036)  |   3    |       3        |       6        | yes (Few questions)  |      2      |
    | attribute3(1037)  |   5    |       5        |       x        | no                   |      X      |
    | attribute4(1038)  |   7    |       1        |       x        | yes (below median)   |      0      |
    | attribute5(1039)  |   8    |       4        |       8        | yes (Few questions)  |      1      |
    | attribute6(1040)  |   1    |       2        |       0        | yes (below median)   |      0      |
    -----------------------------------------------------------------------------------------------------
    |
    |Furthest Score is calculated as: weight multiplied by (maxLevel - currentLevel)
    */
    @Test
    void testRefreshAssessmentAdvice_whenWeakAttributesAreEnoughButNotEnoughQuestions_thenRegenerateAdvice() {
        var adviceListItems1 = createAdviceListItems(3);
        var adviceListItems2 = createAdviceListItems(6);
        var adviceListItems3 = createAdviceListItems(9);
        var adviceListItems4 = createAdviceListItems(10);
        Attribute attribute4 = createWithWeight(7), attribute5 = createWithWeight(8), attribute6 = createWithWeight(1);
        param = createParam(b -> b.forceRegenerate(false));
        assessmentResult = createAssessmentResultWithAssessmentId(param.getAssessmentId());
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(attribute1.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute2.getId(), levelThree().getId()),
            new LoadAttributeValuesPort.Result(attribute3.getId(), levelFive().getId()),
            new LoadAttributeValuesPort.Result(attribute4.getId(), levelOne().getId()),
            new LoadAttributeValuesPort.Result(attribute5.getId(), levelFour().getId()),
            new LoadAttributeValuesPort.Result(attribute6.getId(), levelTwo().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAdviceItemPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(true);
        when(loadAdviceNarrationPort.existsByAssessmentResultId(assessmentResult.getId())).thenReturn(false);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(param.getAssessmentId())))
            .thenReturn(List.of(attribute1, attribute2, attribute3, attribute4, attribute5, attribute6));
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> adviceTargetsCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<QuestionRecommendation>> improvableCaptor = ArgumentCaptor.forClass(List.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<AttributeLevelTarget>> narrationTargetsCaptor = ArgumentCaptor.forClass(List.class);

        when(generateAdvicePlanHelper.createAdvice(eq(param.getAssessmentId()), anyList()))
            .thenReturn(adviceListItems1)
            .thenReturn(adviceListItems2)
            .thenReturn(adviceListItems3)
            .thenReturn(adviceListItems4);

        service.refreshAssessmentAdvice(param);

        verify(generateAdvicePlanHelper, times(4)).createAdvice(eq(param.getAssessmentId()), adviceTargetsCaptor.capture());

        verify(createAiAdviceNarrationHelper).createAiAdviceNarration(
            eq(assessmentResult),
            improvableCaptor.capture(),
            narrationTargetsCaptor.capture()
        );

        List<List<AttributeLevelTarget>> allCalls = adviceTargetsCaptor.getAllValues();

        assertEquals(2, allCalls.get(0).size());
        assertEquals(3, allCalls.get(1).size());
        assertEquals(4, allCalls.get(2).size());

        assertTrue(allCalls.getFirst().stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(allCalls.getFirst().stream().anyMatch(t -> t.getAttributeId() == attribute6.getId()));

        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute6.getId()));
        assertTrue(allCalls.get(1).stream().anyMatch(t -> t.getAttributeId() == attribute5.getId()));

        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute6.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute5.getId()));
        assertTrue(allCalls.get(2).stream().anyMatch(t -> t.getAttributeId() == attribute2.getId()));

        assertEquals(10, improvableCaptor.getValue().size());

        List<AttributeLevelTarget> narratedTargets = narrationTargetsCaptor.getValue();
        assertEquals(5, narratedTargets.size());
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute4.getId()));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute6.getId()));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute5.getId()));
        assertTrue(narratedTargets.stream().anyMatch(t -> t.getAttributeId() == attribute2.getId()));

        verify(deleteAdviceItemPort).deleteAllAiGenerated(assessmentResult.getId());
    }

    @Test
    void testRefreshAssessmentAdvice_whenAttributeHasMaxMaturityLevel_thenNoTargetGenerated() {
        var attributeValues = List.of(new LoadAttributeValuesPort.Result(123L, levelFive().getId()));

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), REFRESH_ASSESSMENT_ADVICE)).thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadMaturityLevelsPort.loadAll(param.getAssessmentId())).thenReturn(allLevels());
        when(loadAttributeValuesPort.loadAll(assessmentResult.getId())).thenReturn(attributeValues);
        when(loadAttributesPort.loadByIdsAndAssessmentId(anyList(), eq(assessmentResult.getAssessmentId()))).thenReturn(List.of(attribute3));

        service.refreshAssessmentAdvice(param);

        verifyNoInteractions(deleteAdviceItemPort,
            generateAdvicePlanHelper,
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

    private List<QuestionRecommendation> createAdviceListItems(int count) {
        return IntStream.range(0, count).mapToObj(i -> QuestionRecommendationMother.createSimpleAdviceListItem()).toList();
    }
}
