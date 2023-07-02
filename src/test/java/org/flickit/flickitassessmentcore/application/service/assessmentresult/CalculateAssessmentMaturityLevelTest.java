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
public class CalculateAssessmentMaturityLevelTest {

    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @Mock
    private LoadMaturityLevelByKitPort loadMaturityLevelByKitPort;
    @Spy
    @InjectMocks
    private CalculateAssessmentMaturityLevel service;

    @Test
    void calculateSubjectMaturityLevel_CalculatedMaturityLevelForSubjectsAs2_MaturityLevel2() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel2());
        when(loadMaturityLevelByKitPort.loadMaturityLevelByKitId(context.getKit().getId())).thenReturn(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2()));
        MaturityLevel ml = service.calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()), context.getKit().getId());
        assertEquals(2, ml.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_CalculatedMaturityLevelForSubjectsAs1_MaturityLevel1() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel1());
        when(loadMaturityLevelByKitPort.loadMaturityLevelByKitId(context.getKit().getId())).thenReturn(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2()));
        MaturityLevel ml = service.calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()), context.getKit().getId());
        assertEquals(1, ml.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_CalculatedMaturityLevelForSubjectNotInKit_ErrorMessage() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel3());
        when(loadMaturityLevelByKitPort.loadMaturityLevelByKitId(context.getKit().getId())).thenReturn(Set.of(context.getMaturityLevel1(), context.getMaturityLevel2()));
        assertThrows(ResourceNotFoundException.class,
            () -> service.calculateAssessmentMaturityLevel(List.of(context.getSubjectValue()), context.getKit().getId()),
            CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE);
    }


}
