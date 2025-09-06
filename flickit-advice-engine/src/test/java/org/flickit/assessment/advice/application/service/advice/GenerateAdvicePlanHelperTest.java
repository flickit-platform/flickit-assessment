package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.SneakyThrows;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestion;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.out.calculation.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort;
import org.flickit.assessment.advice.application.port.out.calculation.LoadCreatedAdviceDetailsPort.Result;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION;
import static org.flickit.assessment.advice.test.fixture.application.QuestionMother.createQuestionWithTargetAndCurrentOption;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateAdvicePlanHelperTest {

    @InjectMocks
    private GenerateAdvicePlanHelper helper;

    @Mock
    private LoadAdviceCalculationInfoPort loadInfoPort;

    @Mock
    private SolverManager<Plan, UUID> solverManager;

    @Mock
    private LoadCreatedAdviceDetailsPort loadCreatedAdviceDetailsPort;

    private final UUID assessmentId = UUID.randomUUID();

    private List<AttributeLevelTarget> attributeLevelTargets =
        List.of(new AttributeLevelTarget(1L, 2L),
            new AttributeLevelTarget(2L, 3L));

    @SneakyThrows
    @Test
    void testCreateAdviceHelper_CalculationInterrupted_ThrowException() {
        attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));

        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);
        var problem = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(loadInfoPort.loadAdviceCalculationInfo(assessmentId, attributeLevelTargets))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        when(solverJob.getFinalBestSolution()).thenThrow(new InterruptedException());

        var throwable = assertThrows(FinalSolutionNotFoundException.class, () -> helper.createAdvice(assessmentId, attributeLevelTargets));
        assertEquals(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION, throwable.getMessage());

        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(assessmentId, attributeLevelTargets);
        verify(solverManager, times(1)).solve(any(), any());
        verifyNoInteractions(loadCreatedAdviceDetailsPort);
    }

    @SneakyThrows
    @Test
    void testCreateAdviceHelper_CalculationExecutionException_ThrowException() {
        attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));

        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);
        var problem = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(loadInfoPort.loadAdviceCalculationInfo(assessmentId, attributeLevelTargets))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        when(solverJob.getFinalBestSolution()).thenThrow(new ExecutionException("", null));

        var throwable = assertThrows(FinalSolutionNotFoundException.class,
            () -> helper.createAdvice(assessmentId, attributeLevelTargets));
        assertEquals(CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION, throwable.getMessage());

        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(assessmentId, attributeLevelTargets);
        verify(solverManager, times(1)).solve(any(), any());

        verifyNoInteractions(
            loadCreatedAdviceDetailsPort
        );
    }

    @SneakyThrows
    @Test
    void testCreateAdviceHelper_whenParametersAreValidParam_ReturnsAdvice() {
        attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));

        mockPorts();

        var result = helper.createAdvice(assessmentId, attributeLevelTargets);
        assertThat(result)
            .allSatisfy(advice -> {
                assertNotNull(advice.recommendedOption());
                assertNotNull(advice.attributes());
                assertNotNull(advice.questionnaire());
                assertFalse(advice.question().title().isBlank());
                assertNotEquals(0, advice.benefit());
            });

        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(assessmentId, attributeLevelTargets);
        verify(solverManager, times(1)).solve(any(), any());
        verify(loadCreatedAdviceDetailsPort, times(1)).loadAdviceDetails(any(), any());
    }

    private void mockPorts() throws InterruptedException, ExecutionException {
        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);
        var problem = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(loadInfoPort.loadAdviceCalculationInfo(assessmentId, attributeLevelTargets))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        question1.setRecommendedOptionIndex(3);
        question2.setRecommendedOptionIndex(3);

        var solution = new Plan(
            List.of(
                attributeLevelScore
            ),
            List.of(
                question1,
                question2
            ));
        when(solverJob.getFinalBestSolution()).thenReturn(solution);

        var adviceQuestion1 = new AdviceQuestion(question1.getId(), "what?", 12);
        var questionnaire = new AdviceQuestionnaire(15L, "Dev ops");
        var attribute = new AdviceAttribute(216L, "Software Efficiency");
        var optionListItems1 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var questionsPortResult1 = new Result(adviceQuestion1, optionListItems1, List.of(attribute), questionnaire);

        var adviceQuestion2 = new AdviceQuestion(question2.getId(), "what?", 15);
        var optionListItems2 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var questionsPortResult2 = new Result(adviceQuestion2, optionListItems2, List.of(attribute), questionnaire);

        when(loadCreatedAdviceDetailsPort.loadAdviceDetails(List.of(question1.getId(), question2.getId()), assessmentId))
            .thenReturn(List.of(questionsPortResult1, questionsPortResult2));
    }
}
