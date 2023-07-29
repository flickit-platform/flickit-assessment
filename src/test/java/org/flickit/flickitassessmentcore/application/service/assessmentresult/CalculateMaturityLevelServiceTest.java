package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateMaturityLevelUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessment.LoadAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadAssessmentResultByAssessmentPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.UpdateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattribute.LoadQualityAttributeBySubjectPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.LoadQualityAttributeByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.qualityattributevalue.UpdateQualityAttributeValuePort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.LoadSubjectValueByResultPort;
import org.flickit.flickitassessmentcore.application.port.out.subjectvalue.UpdateSubjectValuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CalculateMaturityLevelServiceTest {

    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    private final CalculateMaturityLevelUseCase.Param param = new CalculateMaturityLevelUseCase.Param(context.getAssessment().getId());
    @InjectMocks
    private CalculateMaturityLevelService calculateMaturityLevelService;
    @Mock
    private LoadAssessmentPort loadAssessmentPort;
    @Mock
    private LoadAssessmentResultByAssessmentPort loadAssessmentResultByAssessmentPort;
    @Mock
    private UpdateAssessmentResultPort updateAssessmentResultPort;
    @Mock
    private UpdateQualityAttributeValuePort updateQualityAttributeValuePort;
    @Mock
    private UpdateSubjectValuePort updateSubjectValuePort;
    @Mock
    private LoadQualityAttributeBySubjectPort loadQualityAttributeBySubjectPort;
    @Mock
    private LoadQualityAttributeByResultPort loadQualityAttributeByResultPort;
    @Mock
    private LoadSubjectValueByResultPort loadSubjectValueByResultPort;
    @Mock
    private CalculateQualityAttributeMaturityLevel calculateQualityAttributeMaturityLevel;
    @Mock
    private CalculateSubjectMaturityLevel calculateSubjectMaturityLevel;
    @Mock
    private CalculateAssessmentMaturityLevel calculateAssessmentMaturityLevel;
    @Mock
    private LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;


    @Test
    void calculateMaturityLevel_QualityAttributeValueAndSubjectValueAndAssessmentWithMaturityLevel2_MaturityLevel2() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel2());
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel2());
        context.getResult().setMaturityLevelId(context.getMaturityLevel2().getId());
        when(calculateAssessmentMaturityLevel.calculate(any(), any())).thenReturn(context.getMaturityLevel2());
        doMocks();
        CalculateMaturityLevelUseCase.Result result = calculateMaturityLevelService.calculateMaturityLevel(param);
        assertEquals(2, context.getResult().getMaturityLevelId());
        assertEquals(context.getResult().getId(), result.assessmentResultId());
    }

    @Test
    void calculateMaturityLevel_QualityAttributeValueAndSubjectValueAndAssessmentWithMaturityLevel1_MaturityLevel1() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel1());
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel1());
        context.getResult().setMaturityLevelId(context.getMaturityLevel1().getId());
        when(calculateAssessmentMaturityLevel.calculate(any(), any())).thenReturn(context.getMaturityLevel1());
        doMocks();
        CalculateMaturityLevelUseCase.Result result = calculateMaturityLevelService.calculateMaturityLevel(param);
        assertEquals(1, context.getResult().getMaturityLevelId());
        assertEquals(context.getResult().getId(), result.assessmentResultId());
    }

    private void doMocks() {
        when(loadAssessmentPort.load(context.getAssessment().getId())).thenReturn(context.getAssessment());
        when(loadAssessmentResultByAssessmentPort.loadByAssessmentId(context.getAssessment().getId())).thenReturn(new LoadAssessmentResultByAssessmentPort.Result(List.of(context.getResult())));
        when(calculateQualityAttributeMaturityLevel.calculate(context.getResult().getId(), context.getMaturityLevels(), context.getQualityAttribute().getId())).thenReturn(context.getQualityAttributeValue().getMaturityLevel());
        when(calculateSubjectMaturityLevel.calculate(any(), any())).thenReturn(context.getSubjectValue().getMaturityLevel());
        when(updateAssessmentResultPort.update(context.getResult())).thenReturn(context.getResult().getId());
        when(loadQualityAttributeBySubjectPort.loadBySubjectId(any())).thenReturn(new LoadQualityAttributeBySubjectPort.Result(List.of(context.getQualityAttribute())));
        when(loadQualityAttributeByResultPort.loadByResultId(any())).thenReturn(new LoadQualityAttributeByResultPort.Result(List.of(context.getQualityAttributeValue())));
        when(loadSubjectValueByResultPort.loadByResultId(any())).thenReturn(new LoadSubjectValueByResultPort.Result(List.of(context.getSubjectValue())));
        when(loadMaturityLevelByKitPort.loadByKitId(any())).thenReturn(new LoadMaturityLevelByKitPort.Result(context.getMaturityLevels()));
        doNothing().when(updateQualityAttributeValuePort).update(any());
        doNothing().when(updateSubjectValuePort).update(any());
    }
}
