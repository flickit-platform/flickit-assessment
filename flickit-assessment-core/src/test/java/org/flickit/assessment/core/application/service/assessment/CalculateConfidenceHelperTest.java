package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
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
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateConfidenceHelperTest {

    @InjectMocks
    private CalculateConfidenceHelper helper;

    @Mock
    private LoadConfidenceLevelCalculateInfoPort loadConfidenceLevelCalculateInfoPort;

    @Mock
    private UpdateCalculatedConfidencePort updateCalculatedConfidenceLevelResultPort;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Mock
    private LoadSubjectsPort loadSubjectsPort;

    @Mock
    private CreateSubjectValuePort createSubjectValuePort;

    @Mock
    private CreateAttributeValuePort createAttributeValuePort;

    @Test
    void testCalculateConfidenceLevel_ValidInput_ValidResults() {
        List<AttributeValue> s1AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_UNSURE.getId()), //6 questions with 5 answers with cl=1, attrCl=5/30
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_SURE.getId()), //6 questions with 5 answers with cl=5, attrCl = 25/30
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()), //6 questions with 5 answers with cl=3, attrCl = 15/30
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.COMPLETELY_SURE.getId()) //6 questions with 5 answers with cl=5, attrCl = 25/30
        );

        List<AttributeValue> s2AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()), //6 questions with 5 answers with cl=3, attrCl = 15/30
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_SURE.getId()) //6 questions with 5 answers with cl=5, attrCl = 25/30
        );

        List<SubjectValue> subjectValues = List.of(
            SubjectValueMother.withAttributeValues(s1AttributeValues, 1),
            SubjectValueMother.withAttributeValues(s2AttributeValues, 1)
        );

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);

        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);

        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();

        var confidenceValue = helper.calculate(assessmentResult, kitLastMajorModificationTime);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(confidenceValue);
        var sValue1ConfidenceLevel = (((5.0 / 30.0) * 2) + ((25.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) + ((25.0 / 30.0) * 3)) / 10;
        var sValue2ConfidenceLevel = (((15.0 / 30.0) * 4) + ((25.0 / 30.0) * 1)) / 5;
        var resultCl = ((sValue1ConfidenceLevel * 1 + sValue2ConfidenceLevel * 1) / 2) * 100;
        assertEquals(resultCl, confidenceValue, 0.01);
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        List<AttributeValue> s1AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_UNSURE.getId()), //6 questions with 5 answers with cl=1, attrCl=5/30,
            toBeCalcAsConfidenceLevelWithWeight(2, ConfidenceLevel.COMPLETELY_SURE.getId()), //6 questions with 5 answers with cl=5, attrCl = 25/30
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.SOMEWHAT_UNSURE.getId()), //6 questions with 5 answers with cl=3, attrCl = 15/30
            toBeCalcAsConfidenceLevelWithWeight(3, ConfidenceLevel.COMPLETELY_SURE.getId()) //6 questions with 5 answers with cl=5, attrCl = 25/30
        );

        List<AttributeValue> s2AttributeValues = List.of(
            toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()),
            toBeCalcAsConfidenceLevelWithWeight(1, ConfidenceLevel.COMPLETELY_SURE.getId())
        );

        List<SubjectValue> subjectValues = List.of(
            withAttributeValues(s1AttributeValues, 1),
            withAttributeValues(s2AttributeValues, 1)
        );

        var newAttributeValue = toBeCalcAsConfidenceLevelWithWeight(4, ConfidenceLevel.SOMEWHAT_UNSURE.getId()); //6 questions with 5 answers with cl=3, attrCl = 15/30
        var newSubjectValue = withAttributeValues(List.of(), 1);

        List<Subject> subjects = new ArrayList<>(subjectValues.stream().map(SubjectValue::getSubject).toList());
        subjects.add(newSubjectValue.getSubject());

        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());

        var kitLastMajorModificationTime = LocalDateTime.now();
        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anySet(), any())).thenReturn(List.of(newAttributeValue));

        var confidenceValue = helper.calculate(assessmentResult, kitLastMajorModificationTime);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(confidenceValue);
        var sValue1ConfidenceLevel = (((5.0 / 30.0) * 2) + ((25.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) + ((25.0 / 30.0) * 3)) / 10;
        var sValue2ConfidenceLevel = (((15.0 / 30.0) * 4) + ((25.0 / 30.0) * 1) + ((15.0 / 30.0) * 4) )/ 9;
        var sValue3ConfidenceLevel = 0; // Because of sum.getValue() == 0 ? 0 : weightedSum.getValue() / sum.getValue() in attrVal
        var resultCl = ((sValue1ConfidenceLevel * 1 + sValue2ConfidenceLevel * 1 + sValue3ConfidenceLevel) / 3) * 100;
        assertEquals(resultCl, confidenceValue, 0.01);
    }
}
