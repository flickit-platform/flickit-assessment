package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.domain.Subject;
import org.flickit.assessment.core.application.domain.SubjectValue;
import org.flickit.assessment.core.application.port.in.assessment.CalculateAssessmentUseCase;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
import org.flickit.assessment.core.application.port.out.attributevalue.CreateAttributeValuePort;
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
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.CALCULATE_ASSESSMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.*;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.hasFullScoreOnLevel23WithWeight;
import static org.flickit.assessment.core.test.fixture.application.AttributeValueMother.hasFullScoreOnLevel24WithWeight;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelThree;
import static org.flickit.assessment.core.test.fixture.application.MaturityLevelMother.levelTwo;
import static org.flickit.assessment.core.test.fixture.application.SubjectValueMother.withAttributeValues;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CalculateAssessmentServiceTest {

    @InjectMocks
    private CalculateAssessmentService service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

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
    private CreateAttributeValuePort createAttributeValuePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    private final List<AttributeValue> s1AttributeValues = List.of(
        hasFullScoreOnLevel24WithWeight(2, 1533),
        hasFullScoreOnLevel24WithWeight(2, 1534),
        hasFullScoreOnLevel23WithWeight(3, 1535),
        hasFullScoreOnLevel23WithWeight(3, 1536)
    );

    private final List<AttributeValue> s2AttributeValues = List.of(
        hasFullScoreOnLevel24WithWeight(4, 1537),
        hasFullScoreOnLevel23WithWeight(1, 1538)
    );

    private final List<SubjectValue> subjectValues1 = List.of(
        withAttributeValues(s1AttributeValues, 1),
        withAttributeValues(s2AttributeValues, 5)
    );

    private final List<SubjectValue> subjectValues2 = List.of(
        withAttributeValues(s1AttributeValues, 5),
        withAttributeValues(s2AttributeValues, 1)
    );

    @Test
    void testCalculateMaturityLevel_whenParametersAreValid_thenReturnsValidResults() {
        LocalDateTime kitLastMajorModificationTime = LocalDateTime.now();
        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues1);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        UUID currentUserId = UUID.randomUUID();
        var param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId(), currentUserId);

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(kitLastMajorModificationTime);

        var result = service.calculateMaturityLevel(param);

        verify(updateCalculatedResultPort).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort).updateLastModificationTime(any(), any());
        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(levelTwo().getValue(), result.maturityLevel().getValue());
        assertTrue(result.resultAffected());

        verify(loadKitLastMajorModificationTimePort, times(1)).loadLastMajorModificationTime(any());
        verifyNoInteractions(loadSubjectsPort, createSubjectValuePort, createAttributeValuePort);
    }

    @Test
    void testCalculateMaturityLevel_whenKitChanged_thenCreatesNewAttributeAnSubjectValuesAndCalculates() {
        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues2);
        assessmentResult.setLastCalculationTime(LocalDateTime.now());
        UUID currentUserId = UUID.randomUUID();
        var param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId(), currentUserId);

        List<Subject> subjects = new ArrayList<>(subjectValues2.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = hasFullScoreOnLevel23WithWeight(4, 1533);
        var newSubjectValue = withAttributeValues(List.of(newAttributeValue), 2);
        subjects.add(newSubjectValue.getSubject());

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anySet(), any())).thenReturn(List.of(newAttributeValue));

        var result = service.calculateMaturityLevel(param);
        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(levelThree().getValue(), result.maturityLevel().getValue());
        assertTrue(result.resultAffected());

        verify(loadKitLastMajorModificationTimePort, times(1)).loadLastMajorModificationTime(any());
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());
    }

    @Test
    void testCalculateMaturityLevel_whenCalculationTimeIsNull_thenCreatesNewAttributeAnSubjectValuesAndCalculates() {
        AssessmentResult assessmentResult = invalidResultWithSubjectValues(subjectValues2);
        assessmentResult.setLastCalculationTime(null);
        UUID currentUserId = UUID.randomUUID();
        var param = new CalculateAssessmentUseCase.Param(assessmentResult.getAssessment().getId(), currentUserId);

        List<Subject> subjects = new ArrayList<>(subjectValues2.stream().map(SubjectValue::getSubject).toList());
        var newAttributeValue = hasFullScoreOnLevel23WithWeight(4, 1533);
        var newSubjectValue = withAttributeValues(List.of(newAttributeValue), 2);
        subjects.add(newSubjectValue.getSubject());

        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), currentUserId, CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.now());
        when(loadSubjectsPort.loadByKitVersionIdWithAttributes(any())).thenReturn(subjects);
        when(createSubjectValuePort.persistAll(anyList(), any())).thenReturn(List.of(newSubjectValue));
        when(createAttributeValuePort.persistAll(anySet(), any())).thenReturn(List.of(newAttributeValue));

        var result = service.calculateMaturityLevel(param);
        assertNotNull(result);
        assertNotNull(result.maturityLevel());
        assertEquals(levelThree().getValue(), result.maturityLevel().getValue());
        assertTrue(result.resultAffected());

        verify(loadKitLastMajorModificationTimePort, times(1)).loadLastMajorModificationTime(any());
        verify(updateCalculatedResultPort, times(1)).updateCalculatedResult(any(AssessmentResult.class));
        verify(updateAssessmentPort, times(1)).updateLastModificationTime(any(), any());
    }

    @Test
    void testCalculateMaturityLevel_whenCalculationIsValid_resultAffectedIsFalse() {
        var param = new CalculateAssessmentUseCase.Param(UUID.randomUUID(), UUID.randomUUID());
        var assessmentResult = validResult();

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(true);
        when(loadCalculateInfoPort.load(param.getAssessmentId())).thenReturn(assessmentResult);
        when(loadAssessmentResultPort.loadByAssessmentId(param.getAssessmentId())).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(any())).thenReturn(LocalDateTime.MIN);

        var result = service.calculateMaturityLevel(param);
        assertFalse(result.resultAffected());
        assertEquals(assessmentResult.getMaturityLevel(), result.maturityLevel());

        verifyNoInteractions(updateCalculatedResultPort,
            updateAssessmentPort,
            updateCalculatedResultPort,
            createSubjectValuePort,
            createAttributeValuePort);
    }

    @Test
    void testCalculateMaturityLevel_whenCurrentUserDoesNotHaveRequiredPermission_thenThrowsAccessDeniedException() {
        var param = new CalculateAssessmentUseCase.Param(UUID.randomUUID(), UUID.randomUUID());

        when(assessmentAccessChecker.isAuthorized(param.getAssessmentId(), param.getCurrentUserId(), CALCULATE_ASSESSMENT)).thenReturn(false);
        verifyNoInteractions(loadCalculateInfoPort);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.calculateMaturityLevel(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());
    }
}
