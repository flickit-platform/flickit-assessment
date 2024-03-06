package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Result;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectsPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withQAValuesAndSubjectWithQAs;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateConfidenceServiceTest {

    @InjectMocks
    private CalculateConfidenceService service;

    @Mock
    private LoadConfidenceLevelCalculateInfoPort loadConfidenceLevelCalculateInfoPort;

    @Mock
    private UpdateCalculatedConfidencePort updateCalculatedConfidenceLevelResultPort;

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
    void testCalculateConfidenceLevel_ValidInput_ValidResults() {
        List<QualityAttributeValue> s1AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_SURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<QualityAttributeValue> s2AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<SubjectValue> subjectValues = List.of(
            SubjectValueMother.withQAValuesAndSubjectWithQAs(s1AttributeValues, s1AttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList()),
            SubjectValueMother.withQAValuesAndSubjectWithQAs(s2AttributeValues, s2AttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList())
        );

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);

        Param param = new Param(assessmentResult.getAssessment().getId());

        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);

        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(kitLastMajorModificationTime);

        Result result = service.calculate(param);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        double maxPossibleSumConfidence = 75;
        double gainedSumConfidence = 53;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, result.confidenceValue());
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        List<QualityAttributeValue> s1AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_SURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<QualityAttributeValue> s2AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<SubjectValue> subjectValues = List.of(
            withQAValuesAndSubjectWithQAs(s1AttributeValues, s1AttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList()),
            withQAValuesAndSubjectWithQAs(s2AttributeValues, s2AttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList())
        );

        var newAttributeValue = toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId());
        var newSubjectValue = withQAValuesAndSubjectWithQAs(List.of(), List.of(newAttributeValue.getQualityAttribute()));

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        subjects.add(newSubjectValue.getSubject());

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        CalculateConfidenceUseCase.Param param = new CalculateConfidenceUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anyList(), any())).thenReturn(List.of(newAttributeValue));

        CalculateConfidenceUseCase.Result result = service.calculate(param);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.confidenceValue());

        double maxPossibleSumConfidence = 95;
        double gainedSumConfidence = 65;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, result.confidenceValue());
    }
}
