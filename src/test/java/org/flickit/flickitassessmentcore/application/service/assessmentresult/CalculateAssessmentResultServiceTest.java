package org.flickit.flickitassessmentcore.application.service.assessmentresult;

import org.flickit.flickitassessmentcore.application.port.in.assessmentresult.CalculateAssessmentResultUseCase;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.UpdateCalculateResultPort;
import org.flickit.flickitassessmentcore.domain.calculate.AssessmentResult;
import org.flickit.flickitassessmentcore.domain.calculate.QualityAttributeValue;
import org.flickit.flickitassessmentcore.domain.calculate.SubjectValue;
import org.flickit.flickitassessmentcore.domain.calculate.mother.AssessmentResultMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.QualityAttributeValueMother;
import org.flickit.flickitassessmentcore.domain.calculate.mother.SubjectValueMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateAssessmentResultServiceTest {

    @InjectMocks
    private CalculateAssessmentResultService service;

    @Mock
    private LoadCalculateInfoPort loadCalculateInfoPort;

    @Mock
    private UpdateCalculateResultPort updateCalculateResultPort;

    @Test
    void calculateMaturityLevel() {
        List<QualityAttributeValue> s1QualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(2),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(3),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(3)
        );

        List<QualityAttributeValue> s2QualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight(4),
            QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight(1)
        );

        List<SubjectValue> subjectValues = List.of(
            SubjectValueMother.withQAValues(s1QualityAttributeValues),
            SubjectValueMother.withQAValues(s2QualityAttributeValues)
        );


        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        CalculateAssessmentResultUseCase.Param param = new CalculateAssessmentResultUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);

        CalculateAssessmentResultUseCase.Result result = service.calculateMaturityLevel(param);
        verify(updateCalculateResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(4, result.maturityLevel().getLevel());
    }
}
