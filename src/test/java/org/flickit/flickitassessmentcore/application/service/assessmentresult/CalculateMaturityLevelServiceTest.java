package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelCommand;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessment.SaveAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.SaveAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.SaveQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubject.LoadAssessmentSubjectByAssessmentKitPort;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateMaturityLevelServiceTest {
    private final LoadAssessmentPort loadAssessment = mock(LoadAssessmentPort.class);
    private final LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessment = mock(LoadAssessmentResultByAssessmentPort.class);
    private final LoadAssessmentSubjectByAssessmentKitPort loadSubjectByKit = mock(LoadAssessmentSubjectByAssessmentKitPort.class);
    private final LoadQualityAttributeBySubPort loadQualityAttributeBySubject = mock(LoadQualityAttributeBySubPort.class);
    private final SaveAssessmentResultPort saveAssessmentResult = mock(SaveAssessmentResultPort.class);
    private final SaveAssessmentPort saveAssessment = Mockito.mock(SaveAssessmentPort.class);
    private final SaveQualityAttributeValuePort saveQualityAttributeValue = Mockito.mock(SaveQualityAttributeValuePort.class);
    private final SaveAssessmentSubjectValuePort saveAssessmentSubjectValue = Mockito.mock(SaveAssessmentSubjectValuePort.class);
    private final CalculateQualityAttributeMaturityLevel calculateQualityAttributeMaturityLevel = mock(CalculateQualityAttributeMaturityLevel.class);
    private final CalculateAssessmentSubjectMaturityLevel calculateAssessmentSubjectMaturityLevel = mock(CalculateAssessmentSubjectMaturityLevel.class);
    private final CalculateAssessmentMaturityLevel calculateAssessmentMaturityLevel = mock(CalculateAssessmentMaturityLevel.class);
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateMaturityLevelService calculateMaturityLevelService = new CalculateMaturityLevelService(
        loadAssessment,
        loadAssessmentResultByAssessment,
        loadSubjectByKit,
        loadQualityAttributeBySubject,
        saveAssessmentResult,
        saveAssessment,
        saveQualityAttributeValue,
        saveAssessmentSubjectValue,
        calculateQualityAttributeMaturityLevel,
        calculateAssessmentSubjectMaturityLevel,
        calculateAssessmentMaturityLevel);

    private final CalculateMaturityLevelCommand command = new CalculateMaturityLevelCommand(context.getAssessment().getId());

    @Test
    public void calculateMaturityLevelWith2QuestionsResultsInMaturityLevel2_WillSucceed() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel2());
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel2());
        context.getAssessment().setMaturityLevel(context.getMaturityLevel2());
        doReturn(context.getMaturityLevel2()).when(calculateAssessmentMaturityLevel).calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()));
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        AssessmentResult result = calculateMaturityLevelService.calculateMaturityLevel(command);
        assertEquals(2, result.getQualityAttributeValues().get(0).getMaturityLevel().getValue());
        assertEquals(2, result.getAssessmentSubjectValues().get(0).getMaturityLevel().getValue());
        assertEquals(2, result.getAssessment().getMaturityLevel().getValue());
    }

    @Test
    public void calculateMaturityLevelWith2QuestionsResultsInMaturityLevel1_WillSucceed() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel1());
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel1());
        context.getAssessment().setMaturityLevel(context.getMaturityLevel1());
        doReturn(context.getMaturityLevel1()).when(calculateAssessmentMaturityLevel).calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()));
        doMocks();
        // It is possible that sometimes this test doesn't pass, because mocks haven't been applied before service call.
        AssessmentResult result = calculateMaturityLevelService.calculateMaturityLevel(command);
        assertEquals(1, result.getQualityAttributeValues().get(0).getMaturityLevel().getValue());
        assertEquals(1, result.getAssessmentSubjectValues().get(0).getMaturityLevel().getValue());
        assertEquals(1, result.getAssessment().getMaturityLevel().getValue());
    }

    private void doMocks() {
        context.getResult().setQualityAttributeValues(new ArrayList<>());
        context.getResult().setAssessmentSubjectValues(new ArrayList<>());
        doReturn(context.getAssessment()).when(loadAssessment).loadAssessment(context.getAssessment().getId());
        doReturn(Set.of(context.getResult())).when(loadAssessmentResultByAssessment).loadAssessmentResultByAssessmentId(context.getAssessment().getId());
        doReturn(List.of(context.getSubject())).when(loadSubjectByKit).loadSubjectByKitId(context.getKit().getId());
        doReturn(List.of(context.getQualityAttribute())).when(loadQualityAttributeBySubject).loadQABySubId(context.getSubject().getId());
        doReturn(context.getQualityAttributeValue()).when(calculateQualityAttributeMaturityLevel).calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute());
        doReturn(context.getSubjectValue()).when(calculateAssessmentSubjectMaturityLevel).calculateAssessmentSubjectMaturityLevel(context.getSubject());
        doReturn(context.getResult()).when(saveAssessmentResult).saveAssessmentResult(context.getResult());
        doReturn(context.getAssessment()).when(saveAssessment).saveAssessment(context.getAssessment());
        doNothing().when(saveQualityAttributeValue).saveQualityAttributeValue(any());
        doNothing().when(saveAssessmentSubjectValue).saveAssessmentSubjectValue(any());
    }


}
