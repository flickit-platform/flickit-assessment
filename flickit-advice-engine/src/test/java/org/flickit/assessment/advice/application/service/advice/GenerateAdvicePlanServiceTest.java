package org.flickit.assessment.advice.application.service.advice;

import lombok.SneakyThrows;
import org.flickit.assessment.advice.application.domain.AttributeLevelScore;
import org.flickit.assessment.advice.application.domain.AttributeLevelTarget;
import org.flickit.assessment.advice.application.domain.advice.*;
import org.flickit.assessment.advice.application.port.in.advice.GenerateAdvicePlanUseCase;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.attributevalue.LoadAttributeCurrentAndTargetLevelIndexPort;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.port.out.ValidateAssessmentResultPort;
import org.flickit.assessment.common.exception.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;
import java.util.UUID;

import static java.util.UUID.randomUUID;
import static org.flickit.assessment.advice.common.ErrorMessageKey.*;
import static org.flickit.assessment.advice.test.fixture.application.QuestionMother.createQuestionWithTargetAndCurrentOption;
import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CREATE_ADVICE;
import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GenerateAdvicePlanServiceTest {

    @InjectMocks
    private GenerateAdvicePlanService service;

    @Mock
    private ValidateAssessmentResultPort validateAssessmentResultPort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadSelectedAttributeIdsRelatedToAssessmentPort loadSelectedAttributeIdsRelatedToAssessmentPort;

    @Mock
    private LoadSelectedLevelIdsRelatedToAssessmentPort loadSelectedLevelIdsRelatedToAssessmentPort;

    @Mock
    private LoadAttributeCurrentAndTargetLevelIndexPort loadAttributeCurrentAndTargetLevelIndexPort;

    @Mock
    private CreateAdviceHelper createAdviceHelper;

    @Test
    void testGenerate_AssessmentNotExist_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);
        doThrow(new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        assertThrows(ResourceNotFoundException.class, () -> service.generate(param), COMMON_ASSESSMENT_RESULT_NOT_FOUND);

        verifyNoInteractions(createAdviceHelper);
    }

    @Test
    void testGenerate_AssessmentAttributeNotRelated_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets =
            List.of(new AttributeLevelTarget(1L, 2L),
                new AttributeLevelTarget(2L, 3L));
        UUID assessmentId = randomUUID();
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            assessmentId,
            attributeLevelTargets,
            randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);
        when(loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(assessmentId, Set.of(1L, 2L)))
            .thenReturn(Set.of(1L));

        assertThrows(ResourceNotFoundException.class, () -> service.generate(param), CREATE_ADVICE_ASSESSMENT_ATTRIBUTE_RELATION_NOT_FOUND);

        verifyNoInteractions(createAdviceHelper);
    }

    @Test
    void testGenerate_AssessmentMaturityLevelNotRelated_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets =
            List.of(new AttributeLevelTarget(1L, 2L),
                new AttributeLevelTarget(2L, 3L));
        UUID assessmentId = randomUUID();
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            assessmentId,
            attributeLevelTargets,
            randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);
        when(loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(assessmentId, Set.of(1L, 2L)))
            .thenReturn(Set.of(1L, 2L));
        when(loadSelectedLevelIdsRelatedToAssessmentPort.loadSelectedLevelIdsRelatedToAssessment(param.getAssessmentId(), Set.of(2L, 3L)))
            .thenReturn(Set.of(2L));

        assertThrows(ResourceNotFoundException.class, () -> service.generate(param), CREATE_ADVICE_ASSESSMENT_LEVEL_RELATION_NOT_FOUND);

        verifyNoInteractions(createAdviceHelper);
    }

    @Test
    void testGenerate_UserHasNotAccessToAssessment_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.generate(param), COMMON_CURRENT_USER_NOT_ALLOWED);
        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);

        verifyNoInteractions(createAdviceHelper);
    }

    @Test
    void testGenerate_AssessmentCalculateIsNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);

        doThrow(new CalculateNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        assertThrows(CalculateNotValidException.class, () -> service.generate(param), CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);
        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);

        verifyNoInteractions(createAdviceHelper);
    }

    @Test
    void testGenerate_ConfidenceCalculateIsNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);

        doThrow(new ConfidenceCalculationNotValidException(COMMON_ASSESSMENT_RESULT_NOT_VALID))
            .when(validateAssessmentResultPort).validate(param.getAssessmentId());

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.generate(param), CREATE_ADVICE_ASSESSMENT_RESULT_NOT_VALID);

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);

        verifyNoInteractions(createAdviceHelper);
    }

    @SneakyThrows
    @Test
    void testGenerate_AttributeLevelTargetsAreNotValid_ThrowException() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        GenerateAdvicePlanUseCase.Param param = new GenerateAdvicePlanUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);
        when(loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(param.getAssessmentId(), Set.of(1L)))
            .thenReturn(Set.of(1L));
        when(loadSelectedLevelIdsRelatedToAssessmentPort.loadSelectedLevelIdsRelatedToAssessment(param.getAssessmentId(), Set.of(2L)))
            .thenReturn(Set.of(2L));
        when(loadAttributeCurrentAndTargetLevelIndexPort.load(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(1L, 2, 2)));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        assertThrows(ValidationException.class, () -> service.generate(param), CREATE_ADVICE_ATTRIBUTE_LEVEL_TARGETS_SIZE_MIN);

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
        verify(loadAttributeCurrentAndTargetLevelIndexPort, times(1))
            .load(param.getAssessmentId(), param.getAttributeLevelTargets());

        verifyNoInteractions(createAdviceHelper);
    }

    @SneakyThrows
    @Test
    void testGenerate_ValidParam_ReturnsAdvice() {
        List<AttributeLevelTarget> attributeLevelTargets = List.of(new AttributeLevelTarget(1L, 2L));
        var param = new GenerateAdvicePlanUseCase.Param(
            randomUUID(),
            attributeLevelTargets,
            randomUUID()
        );

        mockPorts(param);

        var result = service.generate(param);

        for (QuestionRecommendation question : result.adviceItems()) {
            assertNotNull(question.recommendedOption());
            assertNotNull(question.attributes());
            assertNotNull(question.questionnaire());
            assertFalse(question.question().title().isBlank());
            assertNotEquals(0, question.benefit());
        }

        verify(validateAssessmentResultPort, times(1)).validate(param.getAssessmentId());
        verify(assessmentAccessChecker, times(1)).isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE);
        verify(createAdviceHelper, times(1)).createAdvice(param.getAssessmentId(), param.getAttributeLevelTargets());
    }

    private void mockPorts(GenerateAdvicePlanUseCase.Param param) {
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CREATE_ADVICE))
            .thenReturn(true);
        when(loadSelectedAttributeIdsRelatedToAssessmentPort.loadSelectedAttributeIdsRelatedToAssessment(param.getAssessmentId(), Set.of(1L)))
            .thenReturn(Set.of(1L));
        when(loadSelectedLevelIdsRelatedToAssessmentPort.loadSelectedLevelIdsRelatedToAssessment(param.getAssessmentId(), Set.of(2L)))
            .thenReturn(Set.of(2L));
        when(loadAttributeCurrentAndTargetLevelIndexPort.load(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(new LoadAttributeCurrentAndTargetLevelIndexPort.Result(1L, 2, 3)));
        doNothing().when(validateAssessmentResultPort).validate(param.getAssessmentId());

        var attributeLevelScore = new AttributeLevelScore(2, 12, 1L, 2L);
        var question1 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, null);
        var question2 = createQuestionWithTargetAndCurrentOption(attributeLevelScore, 0);

        question1.setRecommendedOptionIndex(3);
        question2.setRecommendedOptionIndex(3);

        var adviceQuestion1 = new AdviceQuestion(question1.getId(), "what?", 12);
        var questionnaire = new AdviceQuestionnaire(15L, "Dev ops");
        var attribute = new AdviceAttribute(216L, "Software Efficiency");
        var optionListItems1 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var qr1 = new QuestionRecommendation(adviceQuestion1,
            optionListItems1.getFirst(),
            optionListItems1.get(2),
            0.3,
            List.of(attribute), questionnaire);

        var adviceQuestion2 = new AdviceQuestion(question2.getId(), "what?", 15);
        var optionListItems2 = List.of(
            new AdviceOption(1, "caption1"),
            new AdviceOption(2, "caption2"),
            new AdviceOption(3, "caption3"),
            new AdviceOption(4, "caption4")
        );
        var qr2 = new QuestionRecommendation(adviceQuestion2,
            optionListItems2.getFirst(),
            optionListItems2.get(2),
            0.5,
            List.of(attribute), questionnaire);

        when(createAdviceHelper.createAdvice(param.getAssessmentId(), param.getAttributeLevelTargets()))
            .thenReturn(List.of(qr1, qr2));

    }
}
