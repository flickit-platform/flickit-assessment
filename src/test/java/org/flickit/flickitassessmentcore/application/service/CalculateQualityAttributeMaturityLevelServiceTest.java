package org.flickit.flickitassessmentcore.application.service;

import org.flickit.flickitassessmentcore.application.port.out.*;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.HashSet;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

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
    private final CalculateQualityAttributeMaturityLevelService calculateQAMaturityLevelService =
        new CalculateQualityAttributeMaturityLevelService(
            loadQA,
            loadQuestionsByQAId,
            loadAnswerOptionImpactsByAnswerOption,
            loadMLByKit,
            loadQualityAttributeValuesByResult,
            loadAnswersByResult,
            loadLCByML,
            saveQAValue,
            saveAssessmentResult);
    CalculateQAMaturityLevelServiceContext context = new CalculateQAMaturityLevelServiceContext();

    // TODO: This test is in development

    //    @Disabled
    @Test
    public void calculateQualityAttributeWithMaturityLevel2_WillSucceed() {
        when(loadQA.loadQualityAttribute(context.getQualityAttribute().getId())).thenReturn(context.getQualityAttribute());
        when(loadQuestionsByQAId.loadQuestionsByQualityAttributeId(context.getQualityAttribute().getId())).thenReturn(new HashSet<>(List.of(context.getQuestion1(), context.getQuestion2())));
        when(loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOption(context.getOption1Q1().getId())).thenReturn(new HashSet<>(List.of(context.getOptionImpact1Q1())));
        when(loadAnswerOptionImpactsByAnswerOption.findAnswerOptionImpactsByAnswerOption(context.getOption2Q1().getId())).thenReturn(new HashSet<>(List.of(context.getOptionImpact2Q1())));
        when(loadMLByKit.loadMLByKitId(context.getKit().getId())).thenReturn(new HashSet<>(List.of(context.getMaturityLevel1(), context.getMaturityLevel2())));
        when(loadQualityAttributeValuesByResult.loadQualityAttributeValuesByResultId(context.getResult().getId())).thenReturn(new HashSet<>(List.of(context.getQualityAttributeValue())));
        when(loadAnswersByResult.loadAnswersByResultId(context.getResult().getId())).thenReturn(new HashSet<>(List.of(context.getAnswer1(), context.getAnswer2())));
        when(loadLCByML.loadLevelCompetenceByMLId(context.getMaturityLevel2().getId())).thenReturn(new HashSet<>(List.of(context.getLevelCompetence1(), context.getLevelCompetence2())));
        doNothing().when(saveQAValue).saveQualityAttributeValue(context.getQualityAttributeValue());
        doNothing().when(saveAssessmentResult).saveAssessmentResult(context.getResult());

        MaturityLevel maturityLevel = calculateQAMaturityLevelService.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute().getId());
        System.out.println(maturityLevel.toString());
//        assertEquals(2, maturityLevel.getValue());
    }


}
