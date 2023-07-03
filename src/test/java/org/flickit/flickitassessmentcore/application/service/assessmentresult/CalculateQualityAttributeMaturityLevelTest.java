package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswersByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.answeroptionimpact.LoadAnswerOptionImpactsByAnswerOptionPort;
import org.flickit.flickitassessmentcore.application.port.out.levelcompetence.LoadLevelCompetenceByMaturityLevelPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.question.LoadQuestionsByQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.questionImpact.LoadQuestionImpactPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

import static org.flickit.flickitassessmentcore.Constants.ANSWER_OPTION_IMPACT_VALUE4;
import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_MATURITY_LEVEL_ANSWER_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateQualityAttributeMaturityLevelTest {
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @Spy
    @InjectMocks
    private CalculateQualityAttributeMaturityLevel service;
    @Mock
    private LoadQuestionsByQualityAttributePort loadQuestionsByQAId;
    @Mock
    private LoadAnswerOptionImpactsByAnswerOptionPort loadAnswerOptionImpactsByAnswerOption;
    @Mock
    private LoadMaturityLevelByKitPort loadMaturityLevelByKit;
    @Mock
    private LoadAnswersByResultPort loadAnswersByResult;
    @Mock
    private LoadQuestionImpactPort loadQuestionImpact;
    @Mock
    private LoadLevelCompetenceByMaturityLevelPort loadLevelCompetenceByMaturityLevel;

    @Test
    @Disabled
    void calculateQualityAttributeMaturityLevel_Option2ForQuestion1AndOption1ForQuestion2_MaturityLevel2() {
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        MaturityLevel maturityLevel = service.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute());
        assertEquals(2, maturityLevel.getValue());
    }

    @Test
    void calculateQualityAttributeMaturityLevel_Option2ForQuestion1AndOption1ForQuestion2With0Impact_MaturityLevel1() {
        context.getOptionImpact1Q2().setValue(new BigDecimal(0));
        doMocks();
        MaturityLevel maturityLevel = service.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute());
        assertEquals(1, maturityLevel.getValue());
        // Return to former state
        context.getOptionImpact1Q2().setValue(ANSWER_OPTION_IMPACT_VALUE4);
    }

    @Test
    void calculateQualityAttributeMaturityLevel_QuestionWithNullAnswer_ErrorMessage() {
        context.getAnswer2().setQuestionId(0L);
        context.getAnswer1().setQuestionId(0L);
        when(loadQuestionsByQAId.loadQuestionsByQualityAttributeId(any())).thenReturn(new LoadQuestionsByQualityAttributePort.Result(Set.of(context.getQuestion1(), context.getQuestion2())));
        when(loadAnswersByResult.loadAnswersByResultId(context.getResult().getId())).thenReturn(Set.of(context.getAnswer1(), context.getAnswer2()));
        assertThrows(ResourceNotFoundException.class,
            () -> service.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute()),
            CALCULATE_MATURITY_LEVEL_ANSWER_NOT_FOUND_MESSAGE);
        // Return to former state
        context.getAnswer2().setQuestionId(context.getQuestion2().getId());
        context.getAnswer1().setQuestionId(context.getQuestion1().getId());
    }

    private void doMocks() {
        when(loadQuestionsByQAId.loadQuestionsByQualityAttributeId(any())).thenReturn(new LoadQuestionsByQualityAttributePort.Result(Set.of(context.getQuestion1(), context.getQuestion2())));
        when(loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOptionId(new LoadAnswerOptionImpactsByAnswerOptionPort.Param(context.getOption2Q1().getId())))
            .thenReturn(new LoadAnswerOptionImpactsByAnswerOptionPort.Result(Set.of(context.getOptionImpact2Q1())));
        when(loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOptionId(new LoadAnswerOptionImpactsByAnswerOptionPort.Param(context.getOption1Q2().getId())))
            .thenReturn(new LoadAnswerOptionImpactsByAnswerOptionPort.Result(Set.of(context.getOptionImpact1Q2())));
        when(loadMaturityLevelByKit.loadMaturityLevelByKitId(new LoadMaturityLevelByKitPort.Param(context.getKit().getId())))
            .thenReturn(new LoadMaturityLevelByKitPort.Result(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())));
        when(loadAnswersByResult.loadAnswersByResultId(context.getResult().getId())).thenReturn(Set.of(context.getAnswer1(), context.getAnswer2()));
        when(loadLevelCompetenceByMaturityLevel.loadLevelCompetenceByMaturityLevelId(new LoadLevelCompetenceByMaturityLevelPort.Param(context.getMaturityLevel1().getId())))
            .thenReturn(new LoadLevelCompetenceByMaturityLevelPort.Result(new HashSet<>()));
        when(loadLevelCompetenceByMaturityLevel.loadLevelCompetenceByMaturityLevelId(new LoadLevelCompetenceByMaturityLevelPort.Param(context.getMaturityLevel2().getId())))
            .thenReturn(new LoadLevelCompetenceByMaturityLevelPort.Result(Set.of(context.getLevelCompetence1(), context.getLevelCompetence2())));
        when(loadQuestionImpact.loadQuestionImpact(context.getQuestionImpact1().getId())).thenReturn(context.getQuestionImpact1());
        when(loadQuestionImpact.loadQuestionImpact(context.getQuestionImpact2().getId())).thenReturn(context.getQuestionImpact2());
    }


}
