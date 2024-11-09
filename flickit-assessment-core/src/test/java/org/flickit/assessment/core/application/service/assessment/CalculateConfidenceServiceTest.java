package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Param;
import org.flickit.assessment.core.application.port.in.assessment.CalculateConfidenceUseCase.Result;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
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
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CALCULATE_CONFIDENCE;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.invalidResultWithSubjectValues;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.toBeCalcAsConfidenceLevelWithWeight;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateConfidenceServiceTest {

    @InjectMocks
    private CalculateConfidenceService service;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

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
    private CreateAttributeValuePort createAttributeValuePort;

    @Test
    void testCalculateConfidenceLevel_UserHasNotAccess_ThrowsException() {
        UUID assessmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        Param param = new Param(assessmentId, currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_CONFIDENCE)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.calculate(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verifyNoInteractions(loadConfidenceLevelCalculateInfoPort,
            updateCalculatedConfidenceLevelResultPort,
            updateAssessmentPort,
            loadKitLastMajorModificationTimePort,
            loadSubjectsPort,
            createSubjectValuePort,
            createAttributeValuePort
        );
    }

    @Test
    void testCalculateConfidenceLevel_ValidInput_ValidResults() {
        UUID currentUserId = UUID.randomUUID();

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

        Param param = new Param(assessmentResult.getAssessment().getId(), currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_CONFIDENCE)).thenReturn(true);
        when(loadConfidenceLevelCalculateInfoPort.load(assessmentResult.getAssessment().getId())).thenReturn(assessmentResult);
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);

        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(kitLastMajorModificationTime);

        Result result = service.calculate(param);
        verify(updateCalculatedConfidenceLevelResultPort, times(1)).updateCalculatedConfidence(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());

        assertNotNull(result);
        double maxPossibleSumConfidence = (100 * 2) + (100 * 2) + (100 * 3) + (100 * 3) + (100 * 4) + (100 * 1); //1500
        double gainedSumConfidence = (((5.0 / 30.0) * 2) + ((25.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) + ((25.0 / 30.0) * 3) + ((15.0 / 30.0) * 4) + ((25.0 / 30.0) * 1)) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, result.confidenceValue(), 0.01);
    }

    @Test
    void testCalculateMaturityLevel_KitChanged_CreatesNewAttributeAnSubjectValuesAndCalculates() {
        UUID currentUserId = UUID.randomUUID();

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

        CalculateConfidenceUseCase.Param param = new CalculateConfidenceUseCase.Param(assessmentResult.getAssessment().getId(), currentUserId);

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_CONFIDENCE)).thenReturn(true);
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

        double maxPossibleSumConfidence = (100 * 2) + (100 * 2) + (100 * 3) + (100 * 3) + (100 * 4) + (100 * 1) + (100 * 4);
        double gainedSumConfidence = (((5.0 / 30.0) * 2) + ((25.0 / 30.0) * 2) + ((15.0 / 30.0) * 3) +
            ((25.0 / 30.0) * 3) + ((15.0 / 30.0) * 4) + ((25.0 / 30.0) * 1) + (15.0 / 30.0) * 4) * 100;
        double confidenceValue = (gainedSumConfidence / maxPossibleSumConfidence) * 100;
        assertEquals(confidenceValue, result.confidenceValue(), 0.01);
    }
}
