package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.QualityAttributeValue;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother.toBeCalcAsLevelFourWithWeight;
import static org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother.toBeCalcAsLevelThreeWithWeight;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withQAValues;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withQAValuesAndSubjectWithQAs;
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

    @Mock
    private LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateQualityAttributeValuePort createAttributeValuePort;

    @Test
    void testCalculateMaturityLevel_ValidInput_ValidResults() {
        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();

        List<QualityAttributeValue> s1AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelThreeWithWeight(3),
            toBeCalcAsLevelThreeWithWeight(3)
        );

        List<QualityAttributeValue> s2AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(4),
            toBeCalcAsLevelThreeWithWeight(1)
        );

        List<SubjectValue> subjectValues = List.of(
            withQAValues(s1AttributeValues),
            withQAValues(s2AttributeValues)
        );

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        CalculateAssessmentUseCase.Param param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(kitLastMajorModificationTime);

        CalculateAssessmentUseCase.Result result = service.calculateMaturityLevel(param);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(4, result.maturityLevel().getValue());
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        List<QualityAttributeValue> s1AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelFourWithWeight(2),
            toBeCalcAsLevelThreeWithWeight(3),
            toBeCalcAsLevelThreeWithWeight(3)
        );

        List<QualityAttributeValue> s2AttributeValues = List.of(
            toBeCalcAsLevelFourWithWeight(4),
            toBeCalcAsLevelThreeWithWeight(1)
        );

        List<SubjectValue> subjectValues = List.of(
            withQAValuesAndSubjectWithQAs(s1AttributeValues, s1AttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList()),
            withQAValuesAndSubjectWithQAs(s2AttributeValues, s2AttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList())
        );

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = toBeCalcAsLevelFourWithWeight(4);
        var newSubjectValue = withQAValuesAndSubjectWithQAs(List.of(newAttributeValue), List.of(newAttributeValue.getQualityAttribute()));
        subjects.add(newSubjectValue.getSubject());

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        CalculateAssessmentUseCase.Param param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anyList(), any())).thenReturn(List.of(newAttributeValue));

        CalculateAssessmentUseCase.Result result = service.calculateMaturityLevel(param);
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(4, result.maturityLevel().getValue());
    }
}
