package org.flickit.flickitassessmentcore.application.service;

import org.flickit.flickitassessmentcore.application.port.out.*;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
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
    private final CalculateQAMaturityLevelServiceContext context = new CalculateQAMaturityLevelServiceContext();
    private CalculateQualityAttributeMaturityLevelService calculateQAMaturityLevelService = new CalculateQualityAttributeMaturityLevelService(
        loadQA,
        loadQuestionsByQAId,
        loadAnswerOptionImpactsByAnswerOption,
        loadMLByKit,
        loadQualityAttributeValuesByResult,
        loadAnswersByResult,
        loadLCByML,
        saveQAValue,
        saveAssessmentResult);

    // TODO: This test is in development

    @Test
    public void calculateQualityAttributeWithMaturityLevel2_WillSucceed() {
        doMocks();
        // We should wait a little to let the mocks to be applied!
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        MaturityLevel maturityLevel = calculateQAMaturityLevelService.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute().getId());
        assertEquals(2, maturityLevel.getId());
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
    }


}
