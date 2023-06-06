package org.flickit.flickitassessmentcore.application.service;

import org.flickit.flickitassessmentcore.Utils;
import org.flickit.flickitassessmentcore.application.port.out.*;
import org.flickit.flickitassessmentcore.domain.*;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.flickit.flickitassessmentcore.Constants.KIT_ID_1;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateQualityAttributeMaturityLevelServiceTest {
    private final Utils utils = new Utils();
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

    //    @Disabled
    @Test
    public void calculateQualityAttributeWithMaturityLevel1_WillSucceed() {
        AssessmentResult assessmentResult = utils.createAssessmentResult();
        AssessmentKit assessmentKit = utils.createAssessmentKit(KIT_ID_1);
        Utils.completeInitiation(assessmentResult, assessmentKit);
        QualityAttribute qualityAttribute = utils.createQualityAttribute();

//        MaturityLevel maturityLevel = calculateQAMaturityLevelService.calculateQualityAttributeMaturityLevel(assessmentResult, qualityAttribute.getId());

//        assertEquals(1, maturityLevel.getValue());
    }


}
