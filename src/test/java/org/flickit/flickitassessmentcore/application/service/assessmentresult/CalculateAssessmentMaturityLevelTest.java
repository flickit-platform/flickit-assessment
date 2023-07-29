package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.MaturityLevel;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
public class CalculateAssessmentMaturityLevelTest {

    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @InjectMocks
    private CalculateAssessmentMaturityLevel service;

    @Test
    void calculateSubjectMaturityLevel_CalculatedMaturityLevelForSubjectsAs2_MaturityLevel2() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel2());
        MaturityLevel ml = service.calculate(context.getMaturityLevels(), List.of(context.getSubjectValue()));
        assertEquals(2, ml.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_CalculatedMaturityLevelForSubjectsAs1_MaturityLevel1() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel1());
        MaturityLevel ml = service.calculate(context.getMaturityLevels(), List.of(context.getSubjectValue()));
        assertEquals(1, ml.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_CalculatedMaturityLevelForSubjectNotInKit_ErrorMessage() {
        context.getSubjectValue().setMaturityLevel(context.getMaturityLevel3());
        assertThrows(ResourceNotFoundException.class,
            () -> service.calculate(List.of(context.getMaturityLevel1(), context.getMaturityLevel2()), List.of(context.getSubjectValue())),
            CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE);
    }
}
