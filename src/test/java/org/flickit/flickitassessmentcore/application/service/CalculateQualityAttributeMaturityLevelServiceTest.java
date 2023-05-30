package org.flickit.flickitassessmentcore.application.service;

import org.flickit.flickitassessmentcore.Utils;
import org.flickit.flickitassessmentcore.application.port.out.LoadQualityAttributePort;
import org.flickit.flickitassessmentcore.application.port.out.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.domain.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateQualityAttributeMaturityLevelServiceTest {
    private final Utils utils = new Utils();
    private final LoadQualityAttributePort loadQualityAttribute = Mockito.mock(LoadQualityAttributePort.class);
    private final SaveQualityAttributeValuePort saveQualityAttributeValue = Mockito.mock(SaveQualityAttributeValuePort.class);
    private final SaveAssessmentResultPort saveAssessmentResult = Mockito.mock(SaveAssessmentResultPort.class);

    private final CalculateQualityAttributeMaturityLevelService calculateQualityAttributeMaturityLevelService =
        new CalculateQualityAttributeMaturityLevelService(loadQualityAttribute, saveQualityAttributeValue, saveAssessmentResult);

    @Disabled
    @Test
    public void calculateQualityAttributeWithMaturityLevel1_WillSucceed() {
        AssessmentResult assessmentResult = utils.createAssessmentResult();
        QualityAttribute qualityAttribute = utils.createQualityAttribute();

        MaturityLevel maturityLevel = calculateQualityAttributeMaturityLevelService
            .calculateQualityAttributeMaturityLevel(assessmentResult, qualityAttribute.getId());

        assertEquals(1, maturityLevel.getValue());
    }

}
