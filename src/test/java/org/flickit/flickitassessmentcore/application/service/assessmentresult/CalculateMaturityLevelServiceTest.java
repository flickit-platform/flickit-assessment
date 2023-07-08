package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.SaveAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentsubjectvalue.SaveAssessmentSubjectValuePort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.SaveQualityAttributeValuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateMaturityLevelServiceTest {
    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateMaturityLevelUseCase.Param param = new CalculateMaturityLevelUseCase.Param(context.getAssessment().getId());
    @Mock
    private LoadAssessmentPort loadAssessment;
    @Mock
    private LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessment;
    @Mock
    private SaveAssessmentResultPort saveAssessmentResult;
    @Mock
    private SaveQualityAttributeValuePort saveQualityAttributeValue;
    @Mock
    private SaveAssessmentSubjectValuePort saveAssessmentSubjectValue;
    @Mock
    private CalculateQualityAttributeMaturityLevel calculateQualityAttributeMaturityLevel;
    @Mock
    private CalculateAssessmentSubjectMaturityLevel calculateAssessmentSubjectMaturityLevel;
    @Mock
    private CalculateAssessmentMaturityLevel calculateAssessmentMaturityLevel;
    @Spy
    @InjectMocks
    private CalculateMaturityLevelService calculateMaturityLevelService;

    @Test
    void calculateMaturityLevel_QualityAttributeValueAndSubjectValueAndAssessmentWithMaturityLevel2_MaturityLevel2() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel2());
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel2());
        context.getResult().setMaturityLevelId(context.getMaturityLevel2().getId());
        when(calculateAssessmentMaturityLevel.calculateAssessmentMaturityLevel(any(), eq(context.getKit().getId()))).thenReturn(context.getMaturityLevel2());
        doMocks();
        CalculateMaturityLevelUseCase.Result result = calculateMaturityLevelService.calculateMaturityLevel(param);
        assertEquals(2, context.getResult().getQualityAttributeValues().get(0).getMaturityLevel().getValue());
        assertEquals(2, context.getResult().getAssessmentSubjectValues().get(0).getMaturityLevel().getValue());
        assertEquals(2, context.getResult().getMaturityLevelId());
        assertEquals(context.getResult().getId(), result.assessmentResultId());
    }

    @Test
    void calculateMaturityLevel_QualityAttributeValueAndSubjectValueAndAssessmentWithMaturityLevel1_MaturityLevel1() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel1());
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel1());
        context.getResult().setMaturityLevelId(context.getMaturityLevel1().getId());
        when(calculateAssessmentMaturityLevel.calculateAssessmentMaturityLevel(any(), eq(context.getKit().getId()))).thenReturn(context.getMaturityLevel1());
        doMocks();
        CalculateMaturityLevelUseCase.Result result = calculateMaturityLevelService.calculateMaturityLevel(param);
        assertEquals(1, context.getResult().getQualityAttributeValues().get(0).getMaturityLevel().getValue());
        assertEquals(1, context.getResult().getAssessmentSubjectValues().get(0).getMaturityLevel().getValue());
        assertEquals(1, context.getResult().getMaturityLevelId());
        assertEquals(context.getResult().getId(), result.assessmentResultId());
    }

    private void doMocks() {
        when(loadAssessment.loadAssessment(context.getAssessment().getId())).thenReturn(context.getAssessment());
        when(loadAssessmentResultByAssessment.loadAssessmentResultByAssessmentId(context.getAssessment().getId())).thenReturn(Set.of(context.getResult()));
        when(calculateQualityAttributeMaturityLevel.calculateQualityAttributeMaturityLevel(context.getResult(), context.getQualityAttribute(), context.getKit().getId())).thenReturn(context.getQualityAttributeValue().getMaturityLevel());
        when(calculateAssessmentSubjectMaturityLevel.calculateAssessmentSubjectMaturityLevel(any(), eq(context.getKit().getId()))).thenReturn(context.getSubjectValue().getMaturityLevel());
        when(saveAssessmentResult.saveAssessmentResult(context.getResult())).thenReturn(context.getResult());
        doNothing().when(saveQualityAttributeValue).saveQualityAttributeValue(any());
        doNothing().when(saveAssessmentSubjectValue).saveAssessmentSubjectValue(any());
    }


}
