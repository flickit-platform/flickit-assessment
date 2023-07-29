package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.flickit.flickitassessmentcore.Constants.ANSWER_OPTION_IMPACT_VALUE4;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ANSWER_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateQualityAttributeMaturityLevelTest {

    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @InjectMocks
    private CalculateQualityAttributeMaturityLevel service;
    @Mock
    private LoadQuestionsByQualityAttributePort loadQuestionsByQualityAttributePort;
    @Mock
    private LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort;
    @Mock
    private LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort;
    @Mock
    private LoadQuestionImpactPort loadQuestionImpactPort;

    @Test
    @Disabled
    void calculateQualityAttributeMaturityLevel_Option2ForQuestion1AndOption1ForQuestion2_MaturityLevel2() {
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        MaturityLevel maturityLevel = service.calculate(context.getResult().getId(), context.getMaturityLevels(), context.getQualityAttribute().getId());
        assertEquals(2, maturityLevel.getValue());
    }

    @Test
    void calculateQualityAttributeMaturityLevel_Option2ForQuestion1AndOption1ForQuestion2With0Impact_MaturityLevel1() {
        context.getOptionImpact1Q2().setValue(0.0);
        doMocks();
        MaturityLevel maturityLevel = service.calculate(context.getResult().getId(), context.getMaturityLevels(), context.getQualityAttribute().getId());
        assertEquals(1, maturityLevel.getValue());
        // Return to former state
        context.getOptionImpact1Q2().setValue(ANSWER_OPTION_IMPACT_VALUE4);
    }

    @Test
    @Disabled
    void calculateQualityAttributeMaturityLevel_QuestionWithNullAnswer_ErrorMessage() {
        context.getAnswer2().setQuestionId(0L);
        context.getAnswer1().setQuestionId(0L);
        when(loadQuestionsByQualityAttributePort.loadByQualityAttributeId(any())).thenReturn(new LoadQuestionsByQualityAttributePort.Result(List.of(context.getQuestion1(), context.getQuestion2())));
        when(loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.loadAnswerIdAndOptionId(
            context.getResult().getId(), context.getQuestion1().getId()))
            .thenReturn(Optional.of(new LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result(
                context.getAnswer1().getId(), context.getAnswer1().getOptionId())));
        when(loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.loadAnswerIdAndOptionId(
            context.getResult().getId(), context.getQuestion2().getId()))
            .thenReturn(Optional.of(new LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result(
                context.getAnswer2().getId(), context.getAnswer2().getOptionId())));
        assertThrows(ResourceNotFoundException.class,
            () -> service.calculate(context.getResult().getId(), context.getMaturityLevels(), context.getQualityAttribute().getId()),
            CALCULATE_MATURITY_LEVEL_ANSWER_NOT_FOUND_MESSAGE);
        // Return to former state
        context.getAnswer2().setQuestionId(context.getQuestion2().getId());
        context.getAnswer1().setQuestionId(context.getQuestion1().getId());
    }

    private void doMocks() {
        when(loadQuestionsByQualityAttributePort.loadByQualityAttributeId(any())).thenReturn(new LoadQuestionsByQualityAttributePort.Result(List.of(context.getQuestion1(), context.getQuestion2())));
        when(loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort
            .loadByAnswerOptionIdAndQualityAttributeId(context.getOption2Q1().getId(), context.getQualityAttribute().getId()))
            .thenReturn(new LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort.Result(List.of(context.getOptionImpact2Q1())));
        when(loadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort
            .loadByAnswerOptionIdAndQualityAttributeId(context.getOption1Q2().getId(), context.getQualityAttribute().getId()))
            .thenReturn(new LoadAnswerOptionImpactsByAnswerOptionAndQualityAttributePort.Result(List.of(context.getOptionImpact1Q2())));
        when(loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.loadAnswerIdAndOptionId(
            context.getResult().getId(), context.getQuestion1().getId()))
            .thenReturn(Optional.of(new LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result(
                context.getAnswer1().getId(), context.getAnswer1().getOptionId())));
        when(loadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.loadAnswerIdAndOptionId(
            context.getResult().getId(), context.getQuestion2().getId()))
            .thenReturn(Optional.of(new LoadAnswerIdAndOptionIdByAssessmentResultAndQuestionPort.Result(
                context.getAnswer2().getId(), context.getAnswer2().getOptionId())));
        when(loadQuestionImpactPort.load(context.getQuestionImpact1().getId()))
            .thenReturn(new LoadQuestionImpactPort.Result(context.getQuestionImpact1()));
        when(loadQuestionImpactPort.load(context.getQuestionImpact2().getId()))
            .thenReturn(new LoadQuestionImpactPort.Result(context.getQuestionImpact2()));
    }
}
