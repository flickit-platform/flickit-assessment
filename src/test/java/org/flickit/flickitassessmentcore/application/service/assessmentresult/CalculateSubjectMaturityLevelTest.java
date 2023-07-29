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
public class CalculateSubjectMaturityLevelTest {

    private final CalculateMaturityLevelServiceContext context = new CalculateMaturityLevelServiceContext();
    @InjectMocks
    private CalculateSubjectMaturityLevel service;

    @Test
    void calculateSubjectMaturityLevel_2AnsweredQuestionsAnsCalculatedMaturityLevelForQualityAttributesAs2_MaturityLevel2() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel2());
        MaturityLevel maturityLevel = service.calculate(context.getMaturityLevels(), List.of(context.getQualityAttributeValue()));
        assertEquals(2, maturityLevel.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_2AnsweredQuestionsAnsCalculatedMaturityLevelForQualityAttributesAs1_MaturityLevel1() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel1());
        MaturityLevel maturityLevel = service.calculate(context.getMaturityLevels(), List.of(context.getQualityAttributeValue()));
        assertEquals(1, maturityLevel.getValue());
    }

    @Test
    void calculateSubjectMaturityLevel_MaturityLevelNotInKit_ErrorMessage() {
        context.getQualityAttributeValue().setMaturityLevel(context.getMaturityLevel3());
        assertThrows(ResourceNotFoundException.class,
            () -> service.calculate(List.of(context.getMaturityLevel1(), context.getMaturityLevel2()), List.of(context.getQualityAttributeValue())),
            CALCULATE_MATURITY_LEVEL_MATURITY_LEVEL_NOT_FOUND_MESSAGE);
    }
}
