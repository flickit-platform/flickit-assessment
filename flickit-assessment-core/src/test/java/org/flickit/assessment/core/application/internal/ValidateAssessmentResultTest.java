package org.flickit.assessment.core.application.internal;

import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.common.exception.DeprecatedVersionException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidateAssessmentResultTest {

    @InjectMocks
    private ValidateAssessmentResult service;

    @Mock
    private LoadAssessmentResultPort loadAssessmentResultPort;

    @Mock
    private LoadKitLastMajorModificationTimePort loadKitLastMajorModificationTimePort;

    @Test
    void testValidate_assessmentResult_isValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(LocalDateTime.now().minusDays(1));

        service.validate(assessmentId);
    }

    @Test
    void testValidate_assessmentResultNotFound() {
        UUID assessmentId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND))
            .when(loadAssessmentResultPort).loadByAssessmentId(assessmentId);

        assertThrows(ResourceNotFoundException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_FOUND);
        verify(loadAssessmentResultPort, times(1)).loadByAssessmentId(assessmentId);
    }

    @Test
    void testValidate_assessmentResultIsCalculationValid_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult1 = AssessmentResultMother.resultWithValidations(null, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult1));

        assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);

        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.FALSE, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));

        assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);
    }

    @Test
    void testValidate_assessmentResultIsConfCalculationValid_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult1 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, null, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult1));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(LocalDateTime.now().minusDays(1));

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);

        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.FALSE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(LocalDateTime.now().minusDays(1));

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);
    }

    @Test
    void testValidate_assessmentResultLastCalculationTime_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult1 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, null, LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult1));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(LocalDateTime.now());

        assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);

        LocalDateTime lastKitMajorModificationTime = LocalDateTime.now();
        LocalDateTime lastCalculationTime = lastKitMajorModificationTime.minusDays(1);
        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, lastCalculationTime, LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(lastKitMajorModificationTime);

        assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);
    }

    @Test
    void testValidate_assessmentResultLastConfCalculationTime_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        LocalDateTime lastKitMajorModificationTime = LocalDateTime.now();
        LocalDateTime lastCalculationTime = lastKitMajorModificationTime.plusDays(1);

        AssessmentResult assessmentResult = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, lastCalculationTime, null);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(lastKitMajorModificationTime);

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);

        LocalDateTime lastConfCalcTime = lastKitMajorModificationTime.minusDays(1);
        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, lastCalculationTime, lastConfCalcTime);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(lastKitMajorModificationTime);

        assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId), COMMON_ASSESSMENT_RESULT_NOT_VALID);
    }

    @Test
    void testValidate_assessmentResultKitVersionIsDeprecated_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.resultWithDeprecatedKitVersion(Boolean.TRUE, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));

        var exception = assertThrows(DeprecatedVersionException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_KIT_VERSION_DEPRECATED, exception.getMessage());
    }
}
