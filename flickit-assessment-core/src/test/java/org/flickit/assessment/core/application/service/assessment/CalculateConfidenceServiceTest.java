package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Result;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.qualityattributevalue.CreateQualityAttributeValuePort;
import org.flickit.assessment.core.application.port.out.subject.LoadSubjectPort;
import org.flickit.assessment.core.application.port.out.subjectvalue.CreateSubjectValuePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.flickit.assessment.core.test.fixture.application.QualityAttributeValueMother;
import org.flickit.assessment.core.test.fixture.application.SubjectValueMother;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitLastEffectiveModificationTimePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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
    private LoadKitLastEffectiveModificationTimePort loadKitLastEffectiveModificationTimePort;

    @Mock
    private LoadSubjectPort loadSubjectPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateQualityAttributeValuePort createQualityAttributeValuePort;

    @Test
    void testCalculateConfidenceLevel_ValidInput_ValidResults() {
        LocalDateTime kitLastEffectiveModificationTime = LocalDateTime.now();

        List<QualityAttributeValue> s1QualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<QualityAttributeValue> s2QualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<SubjectValue> subjectValues = List.of(
            SubjectValueMother.withQAValues(s1QualityAttributeValues),
            SubjectValueMother.withQAValues(s2QualityAttributeValues)
        );


        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);

        Param param = new Param(assessmentResult.getAssessment().getId());

        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadKitLastEffectiveModificationTimePort.load(any())).thenReturn(kitLastEffectiveModificationTime);

        Result result = service.calculate(param);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertEquals(70.666666666666666, result.confidenceValue());
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        List<QualityAttributeValue> s1QualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_SURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<QualityAttributeValue> s2QualityAttributeValues = List.of(
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<SubjectValue> subjectValues = List.of(
            SubjectValueMother.withQAValuesAndSubjectWithQAs(s1QualityAttributeValues, s1QualityAttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList()),
            SubjectValueMother.withQAValuesAndSubjectWithQAs(s2QualityAttributeValues, s2QualityAttributeValues.stream().map(QualityAttributeValue::getQualityAttribute).toList())
        );

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = QualityAttributeValueMother.toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId());
        var newSubjectValue = SubjectValueMother.withQAValuesAndSubjectWithQAs(List.of(newAttributeValue), List.of(newAttributeValue.getQualityAttribute()));
        subjects.add(newSubjectValue.getSubject());

        AssessmentResult assessmentResult = AssessmentResultMother.invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        CalculateConfidenceUseCase.Param param = new CalculateConfidenceUseCase.Param(assessmentResult.getAssessment().getId());

        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadKitLastEffectiveModificationTimePort.load(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectPort.loadByKitIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createQualityAttributeValuePort.persistAll(anyList(), any())).thenReturn(List.of(newAttributeValue));

        CalculateConfidenceUseCase.Result result = service.calculate(param);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        assertNotNull(result.confidenceValue());
        assertEquals(65.16129032258064, result.confidenceValue());
    }
}
