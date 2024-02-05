package org.flickit.assessment.advice.application.service.advice;

import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.SneakyThrows;
import org.flickit.assessment.advice.application.domain.AssessmentResult;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.advice.AdviceAttribute;
import org.flickit.assessment.advice.application.domain.advice.AdviceListItem;
import org.flickit.assessment.advice.application.domain.advice.AdviceOption;
import org.flickit.assessment.advice.application.domain.advice.AdviceQuestionnaire;
import org.flickit.assessment.advice.application.exception.FinalSolutionNotFoundException;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase;
import org.flickit.assessment.advice.application.port.in.CreateAdviceUseCase.AttributeLevelTarget;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultPort;
import org.flickit.assessment.advice.application.port.out.assessment.UserAssessmentAccessibilityPort;
import org.flickit.assessment.advice.application.port.out.question.LoadAdviceImpactfulQuestionsPort;
import org.flickit.assessment.advice.application.port.out.question.LoadAdviceImpactfulQuestionsPort.Result;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.UUID.randomUUID;
import static org.flickit.assessment.advice.application.service.QuestionMother.createQuestionWithTargetAndCurrentOption;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.advice.common.ErrorMessageKey.CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreateAdviceServiceTest {

    @InjectMocks
    private CreateAdviceService service;

    @Mock
    private UserAssessmentAccessibilityPort userAssessmentAccessibilityPort;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadAdviceCalculationInfoPort loadInfoPort;

    @Mock
    private SolverManager<Plan, UUID> solverManager;

    @Mock
    private LoadAdviceImpactfulQuestionsPort loadAdviceImpactfulQuestionsPort;

    @Test
    void testCreateAdvice_UserHasNotAccessToAssessment_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(userAssessmentAccessibilityPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.createAdvice(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        Mockito.verifyNoInteractions(
            loadAssessmentResultPort,
            loadInfoPort,
            solverManager,
            loadAdviceImpactfulQuestionsPort
        );
    }

    @Test
    void testCreateAdvice_AssessmentCalculateIsNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(userAssessmentAccessibilityPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(new AssessmentResult(UUID.randomUUID(), false, true)));

        assertThrows(CalculateNotValidException.class, () -> service.createAdvice(param), CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadAdviceImpactfulQuestionsPort
        );
    }

    @Test
    void testCreateAdvice_ConfidenceCalculateIsNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(userAssessmentAccessibilityPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(new AssessmentResult(UUID.randomUUID(), true, false)));

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.createAdvice(param), CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);

        verify(userAssessmentAccessibilityPort, times(1)).hasAccess(param.getAssessmentId(), param.getCurrentUserId());
        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadAdviceImpactfulQuestionsPort
        );
    }

    @SneakyThrows
    @Test
    void testCreateAdvice_CalculationInterrupted_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(userAssessmentAccessibilityPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(new AssessmentResult(UUID.randomUUID(), true, true)));

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
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        when(solverJob.getFinalBestSolution()).thenThrow(new InterruptedException());

        assertThrows(FinalSolutionNotFoundException.class, () -> service.createAdvice(param), CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);

        verify(userAssessmentAccessibilityPort, times(1)).hasAccess(param.getAssessmentId(), param.getCurrentUserId());
        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        verify(solverManager, times(1)).solve(any(), any());
        Mockito.verifyNoInteractions(
            loadAdviceImpactfulQuestionsPort
        );
    }
    @SneakyThrows
    @Test
    void testCreateAdvice_CalculationExecutionException_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        CreateAdviceUseCase.Param param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(userAssessmentAccessibilityPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(new AssessmentResult(UUID.randomUUID(), true, true)));

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
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(problem);

        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        when(solverJob.getFinalBestSolution()).thenThrow(new ExecutionException("", null));

        assertThrows(FinalSolutionNotFoundException.class, () -> service.createAdvice(param), CREATE_ADVICE_FINDING_BEST_SOLUTION_EXCEPTION);

        verify(userAssessmentAccessibilityPort, times(1)).hasAccess(param.getAssessmentId(), param.getCurrentUserId());
        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        verify(solverManager, times(1)).solve(any(), any());
        Mockito.verifyNoInteractions(
            loadAdviceImpactfulQuestionsPort
        );
    }

    @SneakyThrows
    @Test
    void testCreateAdvice_ValidParam_ReturnsAdvice() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        var param = new CreateAdviceUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        mockPorts(param);

        var result = service.createAdvice(param);

        for (AdviceListItem question : result.questions()) {
            assertNotNull(question.recommendedOption());
            assertNotNull(question.attributes());
            assertNotNull(question.questionnaire());
            assertFalse(question.question().title().isBlank());
            assertNotEquals(0, question.benefit());
        }

        verify(userAssessmentAccessibilityPort, times(1)).hasAccess(param.getAssessmentId(), param.getCurrentUserId());
        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets());
        verify(solverManager, times(1)).solve(any(), any());
        verify(loadAdviceImpactfulQuestionsPort, times(1)).loadQuestions(any());
    }

    private void mockPorts(CreateAdviceUseCase.Param param) throws InterruptedException, ExecutionException {
        when(userAssessmentAccessibilityPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId()))
            .thenReturn(Optional.of(new AssessmentResult(UUID.randomUUID(), true, true)));

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
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getAttributeLevelTargets()))
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


        var questionnaire = new AdviceQuestionnaire(15L, "Dev ops");
        var attribute = new AdviceAttribute(216L, "Software Efficiency");
        var optionListItems1 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var questionsPortResult1 = new Result(question1.getId(), "what?", 12, optionListItems1, List.of(attribute), questionnaire);

        var optionListItems2 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var questionsPortResult2 = new Result(question2.getId(), "what?", 15, optionListItems2, List.of(attribute), questionnaire);
        when(loadAdviceImpactfulQuestionsPort.loadQuestions(List.of(question1.getId(), question2.getId())))
            .thenReturn(List.of(questionsPortResult1, questionsPortResult2));
    }
}
