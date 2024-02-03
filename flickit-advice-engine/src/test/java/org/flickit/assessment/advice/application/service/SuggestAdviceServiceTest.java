package org.flickit.assessment.advice.application.service;

import ai.timefold.solver.core.api.solver.SolverJob;
import ai.timefold.solver.core.api.solver.SolverManager;
import lombok.SneakyThrows;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.Plan;
import org.flickit.assessment.advice.application.domain.advice.AttributeListItem;
import org.flickit.assessment.advice.application.domain.advice.OptionListItem;
import org.flickit.assessment.advice.application.domain.advice.QuestionListItem;
import org.flickit.assessment.advice.application.domain.advice.QuestionnaireListItem;
import org.flickit.assessment.advice.application.port.in.SuggestAdviceUseCase;
import org.flickit.assessment.advice.application.port.out.LoadAdviceCalculationInfoPort;
import org.flickit.assessment.advice.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentResultValidationFieldsPort;
import org.flickit.assessment.advice.application.port.out.question.LoadQuestionsPort;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static java.util.UUID.randomUUID;
import static org.flickit.assessment.advice.application.service.QuestionMother.createQuestionWithTargetAndCurrentOption;
import static org.flickit.assessment.advice.common.ErrorMessageKey.SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_VALID;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SuggestAdviceServiceTest {

    @InjectMocks
    private SuggestAdviceService service;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Mock
    private LoadAssessmentResultValidationFieldsPort loadAssessmentResultValidationFieldsPort;

    @Mock
    private LoadAdviceCalculationInfoPort loadInfoPort;

    @Mock
    private SolverManager<Plan, UUID> solverManager;

    @Mock
    private LoadQuestionsPort loadQuestionsPort;

    @Test
    void testSuggestAdvice_UserHasNotAccessToAssessment_ThrowException() {
        HashMap<Long, Long> targets = new HashMap<>();
        targets.put(1L, 2L);
        SuggestAdviceUseCase.Param param = new SuggestAdviceUseCase.Param(
            randomUUID(),
            targets,
            randomUUID()
        );

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.suggestAdvice(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        Mockito.verifyNoInteractions(
            loadAssessmentResultValidationFieldsPort,
            loadInfoPort,
            solverManager,
            loadQuestionsPort
        );
    }

    @Test
    void testSuggestAdvice_AssessmentCalculateIsNotValid_ThrowException() {
        HashMap<Long, Long> targets = new HashMap<>();
        targets.put(1L, 2L);
        SuggestAdviceUseCase.Param param = new SuggestAdviceUseCase.Param(
            randomUUID(),
            targets,
            randomUUID()
        );

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);

        when(loadAssessmentResultValidationFieldsPort.loadValidationFields(param.getAssessmentId()))
            .thenReturn(new LoadAssessmentResultValidationFieldsPort.Result(false, true));

        assertThrows(CalculateNotValidException.class, () -> service.suggestAdvice(param), SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadQuestionsPort
        );
    }

    @Test
    void testSuggestAdvice_ConfidenceCalculateIsNotValid_ThrowException() {
        HashMap<Long, Long> targets = new HashMap<>();
        targets.put(1L, 2L);
        SuggestAdviceUseCase.Param param = new SuggestAdviceUseCase.Param(
            randomUUID(),
            targets,
            randomUUID()
        );

        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);

        when(loadAssessmentResultValidationFieldsPort.loadValidationFields(param.getAssessmentId()))
            .thenReturn(new LoadAssessmentResultValidationFieldsPort.Result(true, false));

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.suggestAdvice(param), SUGGEST_ADVICE_ASSESSMENT_RESULT_NOT_VALID);

        verify(checkUserAssessmentAccessPort, times(1)).hasAccess(param.getAssessmentId(), param.getCurrentUserId());
        verify(loadAssessmentResultValidationFieldsPort, times(1)).loadValidationFields(param.getAssessmentId());
        Mockito.verifyNoInteractions(
            loadInfoPort,
            solverManager,
            loadQuestionsPort
        );
    }

    @SneakyThrows
    @Test
    void testSuggestAdvice_ValidParam_ReturnsAdvice() {
        HashMap<Long, Long> targets = new HashMap<>();
        targets.put(1L, 2L);
        var param = new SuggestAdviceUseCase.Param(
            randomUUID(),
            targets,
            randomUUID()
        );

        mockPorts(param);

        var result = service.suggestAdvice(param);

        for (QuestionListItem question : result.questions()) {
            assertNotNull(question.options());
            assertNotNull(question.attributes());
            assertNotNull(question.questionnaire());
            assertFalse(question.title().isBlank());
            assertNotEquals(0, question.recommendedOptionIndex());
            assertNotEquals(0, question.benefit());
        }

        verify(checkUserAssessmentAccessPort, times(1)).hasAccess(param.getAssessmentId(), param.getCurrentUserId());
        verify(loadAssessmentResultValidationFieldsPort, times(1)).loadValidationFields(param.getAssessmentId());
        verify(loadInfoPort, times(1)).loadAdviceCalculationInfo(param.getAssessmentId(), param.getTargets());
        verify(solverManager, times(1)).solve(any(), any());
        verify(loadQuestionsPort, times(1)).loadQuestions(any());
    }

    private void mockPorts(SuggestAdviceUseCase.Param param) throws InterruptedException, ExecutionException {
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCurrentUserId()))
            .thenReturn(true);
        when(loadAssessmentResultValidationFieldsPort.loadValidationFields(param.getAssessmentId()))
            .thenReturn(new LoadAssessmentResultValidationFieldsPort.Result(true, true));

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
        when(loadInfoPort.loadAdviceCalculationInfo(param.getAssessmentId(), param.getTargets()))
            .thenReturn(problem);


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
        SolverJob<Plan, UUID> solverJob = Mockito.mock(SolverJob.class);
        when(solverJob.getFinalBestSolution()).thenReturn(solution);

        when(solverManager.solve(any(), eq(problem))).thenReturn(solverJob);

        var questionnaire = new QuestionnaireListItem(15L, "Dev ops");
        var attribute = new AttributeListItem(216L, "Software Efficiency");
        var optionListItems1 = List.of(
            new OptionListItem(1, "caption1"),
            new OptionListItem(2, "caption2"),
            new OptionListItem(3, "caption3"),
            new OptionListItem(4, "caption4")
        );
        var questionsPortResult1 = new LoadQuestionsPort.Result(0L, "what?", 12, optionListItems1, List.of(attribute), questionnaire);

        var optionListItems2 = List.of(
            new OptionListItem(1, "caption1"),
            new OptionListItem(2, "caption2"),
            new OptionListItem(3, "caption3"),
            new OptionListItem(4, "caption4")
        );
        var questionsPortResult2 = new LoadQuestionsPort.Result(1L, "what?", 15, optionListItems2, List.of(attribute), questionnaire);
        when(loadQuestionsPort.loadQuestions(List.of(0L, 1L))).thenReturn(List.of(questionsPortResult1, questionsPortResult2));
    }
}
