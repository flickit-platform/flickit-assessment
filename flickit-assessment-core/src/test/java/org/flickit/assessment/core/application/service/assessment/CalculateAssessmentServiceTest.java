package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
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
class CalculateAssessmentServiceTest {

    @InjectMocks
    private CalculateAssessmentService service;

    @Mock
    private LoadCalculateInfoPort loadCalculateInfoPort;

    @Mock
    private UpdateCalculatedResultPort updateCalculatedResultPort;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Test
    void testCalculateMaturityLevel_ValidInput_ValidResults() {
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

        CalculateAssessmentUseCase.Param param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);

        CalculateAssessmentUseCase.Result result = service.calculateMaturityLevel(param);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(4, result.maturityLevel().getValue());
    }
}
