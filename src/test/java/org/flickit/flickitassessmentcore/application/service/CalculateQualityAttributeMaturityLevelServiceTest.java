package org.flickit.flickitassessmentcore.application.service;

import org.flickit.flickitassessmentcore.application.port.in.qualityattribute.CalculateQAMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.out.*;
import org.flickit.flickitassessmentcore.application.service.exception.NoAnswerFoundException;
import org.flickit.flickitassessmentcore.application.service.qualityattribute.CalculateQualityAttributeMaturityLevelService;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;

import static org.flickit.flickitassessmentcore.Constants.ANSWER_OPTION_IMPACT_VALUE4;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateQualityAttributeMaturityLevelServiceTest {
    private final LoadQualityAttributePort loadQA = Mockito.mock(LoadQualityAttributePort.class);
    private final LoadQuestionsByQAIdPort loadQuestionsByQAId = Mockito.mock(LoadQuestionsByQAIdPort.class);
    private final LoadAnswerOptionImpactsByAnswerOptionPort loadAnswerOptionImpactsByAnswerOption = Mockito.mock(LoadAnswerOptionImpactsByAnswerOptionPort.class);
    private final LoadMLByKitPort loadMLByKit = Mockito.mock(LoadMLByKitPort.class);
    private final LoadQualityAttributeValuesByResultPort loadQualityAttributeValuesByResult = Mockito.mock(LoadQualityAttributeValuesByResultPort.class);
    private final LoadAnswersByResultPort loadAnswersByResult = Mockito.mock(LoadAnswersByResultPort.class);
    private final LoadLevelCompetenceByMLPort loadLCByML = Mockito.mock(LoadLevelCompetenceByMLPort.class);
    private final SaveQualityAttributeValuePort saveQAValue = Mockito.mock(SaveQualityAttributeValuePort.class);
    private final SaveAssessmentResultPort saveAssessmentResult = Mockito.mock(SaveAssessmentResultPort.class);
    private final LoadAssessmentResultPort loadAssessmentResult = Mockito.mock(LoadAssessmentResultPort.class);
    private final CalculateQAMaturityLevelServiceContext context = new CalculateQAMaturityLevelServiceContext();
    private final CalculateQualityAttributeMaturityLevelService calculateQAMaturityLevelService = new CalculateQualityAttributeMaturityLevelService(
        loadQA,
        loadQuestionsByQAId,
        loadAnswerOptionImpactsByAnswerOption,
        loadMLByKit,
        loadQualityAttributeValuesByResult,
        loadAnswersByResult,
        loadLCByML,
        saveQAValue,
        saveAssessmentResult,
        loadAssessmentResult);

    private final CalculateQAMaturityLevelCommand command = new CalculateQAMaturityLevelCommand(
        context.getQualityAttribute().getId(),
        context.getResult().getId());

    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInMaturityLevel2_WillSucceed() {
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        MaturityLevel maturityLevel = calculateQAMaturityLevelService.calculateQualityAttributeMaturityLevel(command);
        assertEquals(2, maturityLevel.getId());
    }

    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInMaturityLevel1_WillSucceed() {
        context.getOptionImpact1Q2().setValue(new BigDecimal(0));
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        MaturityLevel maturityLevel = calculateQAMaturityLevelService.calculateQualityAttributeMaturityLevel(command);
        assertEquals(1, maturityLevel.getId());
        // Return to former state
        context.getOptionImpact1Q2().setValue(ANSWER_OPTION_IMPACT_VALUE4);
    }

    @Test
    public void calculateQualityAttributeMaturityLevelWith2QuestionsResultsInNoAnswerException_WillFail() {
        context.getAnswer2().setQuestion(null);
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        assertThrows(NoAnswerFoundException.class, () -> calculateQAMaturityLevelService.calculateQualityAttributeMaturityLevel(command));
        // Return to former state
        context.getAnswer2().setQuestion(context.getQuestion2());
    }

    private void doMocks() {
        doReturn(context.getQualityAttribute()).when(loadQA).loadQualityAttribute(context.getQualityAttribute().getId());
        doReturn(new HashSet<>(List.of(context.getQuestion1(), context.getQuestion2()))).when(loadQuestionsByQAId).loadQuestionsByQualityAttributeId(context.getQualityAttribute().getId());
        doReturn(new HashSet<>(List.of(context.getOptionImpact2Q1()))).when(loadAnswerOptionImpactsByAnswerOption).findAnswerOptionImpactsByAnswerOption(context.getOption2Q1().getId());
        doReturn(new HashSet<>(List.of(context.getOptionImpact1Q2()))).when(loadAnswerOptionImpactsByAnswerOption).findAnswerOptionImpactsByAnswerOption(context.getOption1Q2().getId());
        doReturn(new HashSet<>(List.of(context.getMaturityLevel1(), context.getMaturityLevel2()))).when(loadMLByKit).loadMLByKitId(context.getKit().getId());
        doReturn(new HashSet<>(List.of(context.getQualityAttributeValue()))).when(loadQualityAttributeValuesByResult).loadQualityAttributeValuesByResultId(context.getResult().getId());
        doReturn(new HashSet<>(List.of(context.getAnswer1(), context.getAnswer2()))).when(loadAnswersByResult).loadAnswersByResultId(context.getResult().getId());
        doReturn(new HashSet<>(List.of(context.getLevelCompetence1(), context.getLevelCompetence2()))).when(loadLCByML).loadLevelCompetenceByMLId(context.getMaturityLevel2().getId());
        doNothing().when(saveQAValue).saveQualityAttributeValue(context.getQualityAttributeValue());
        doNothing().when(saveAssessmentResult).saveAssessmentResult(context.getResult());
        doReturn(context.getResult()).when(loadAssessmentResult).loadResult(context.getResult().getId());
    }


}
