package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.out.maturitylevel.LoadMaturityLevelByKitPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Set;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CalculateAssessmentSubjectMaturityLevelTest {

    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @Mock
    private LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;
    @Spy
    @InjectMocks
    private CalculateAssessmentSubjectMaturityLevel service;

    @Test
    void calculateSubjectMaturityLevel_2AnsweredQuestionsAnsCalculatedMaturityLevelForQualityAttributesAs2_MaturityLevel2() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel2());
        when(loadMaturityLevelByKitPort.loadMaturityLevelByKitId(new LoadMaturityLevelByKitPort.Param(context.getKit().getId())))
            .thenReturn(new LoadMaturityLevelByKitPort.Result(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())));
        MaturityLevel maturityLevel = service.calculateAssessmentSubjectMaturityLevel(List.of(context.getQualityAttributeValue()), context.getKit().getId());
        assertEquals(2, maturityLevel.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_2AnsweredQuestionsAnsCalculatedMaturityLevelForQualityAttributesAs1_MaturityLevel1() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel1());
        when(loadMaturityLevelByKitPort.loadMaturityLevelByKitId(new LoadMaturityLevelByKitPort.Param(context.getKit().getId())))
            .thenReturn(new LoadMaturityLevelByKitPort.Result(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())));
        MaturityLevel maturityLevel = service.calculateAssessmentSubjectMaturityLevel(List.of(context.getQualityAttributeValue()), context.getKit().getId());
        assertEquals(1, maturityLevel.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_MaturityLevelNotInKit_ErrorMessage() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel3());
        when(loadMaturityLevelByKitPort.loadMaturityLevelByKitId(new LoadMaturityLevelByKitPort.Param(context.getKit().getId())))
            .thenReturn(new LoadMaturityLevelByKitPort.Result(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2())));
        assertThrows(ResourceNotFoundException.class,
            () -> service.calculateAssessmentSubjectMaturityLevel(List.of(context.getQualityAttributeValue()), context.getKit().getId()),
            CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE);
    }


}
