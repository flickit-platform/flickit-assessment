package org.flickit.flickitassessmentcore.application.service;

import org.flickit.flickitassessmentcore.application.port.in.CalculateQualityAttributeMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentKitPort;
import org.flickit.flickitassessmentcore.application.port.out.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.domain.*;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.Constants.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CalculateMaturityLevelServiceTest {

    private final LoadAssessmentPort loadAssessment = Mockito.mock(LoadAssessmentPort.class);
    private final LoadAssessmentKitPort loadAssessmentKitPort = Mockito.mock(LoadAssessmentKitPort.class);

    private final CalculateAssessmentMaturityLevelService calculateMaturityLevelService =
        new CalculateAssessmentMaturityLevelService(loadAssessment, loadAssessmentKitPort);

    @Disabled
    @Test
    public void calculateGivenAssessmentIdMaturityLevel1_WillSucceed() {
        AssessmentColor assessmentColor = new AssessmentColor(COLOR_ID, COLOR_TITLE, COLOR_CODE, new ArrayList<>());
        AssessmentKit assessmentKit = new AssessmentKit();
        MaturityLevel maturityLevel1 = new MaturityLevel(MATURITY_LEVEL_ID,
            MATURITY_LEVEL_TITLE,
            MATURITY_LEVEL_VALUE,
            assessmentKit,
            new HashSet<Assessment>(),
            new HashSet<QualityAttributeValue>(),
            new HashSet<QuestionImpact>());
        Assessment assessment = new Assessment(UUID.randomUUID(),
            CODE,
            TITLE,
            DESCRIPTION,
            LocalDateTime.now(),
            LocalDateTime.now(),
            assessmentKit,
            assessmentColor,
            SPACE_ID,
            maturityLevel1,
            new HashSet<AssessmentResult>(),
            new HashSet<Evidence>());

        assessmentColor.getAssessments().add(assessment);

        MaturityLevel maturityLevel = calculateMaturityLevelService.calculateAssessmentMaturityLevel(assessment.getId());

        assertEquals(1, maturityLevel.getValue());
    }
}
