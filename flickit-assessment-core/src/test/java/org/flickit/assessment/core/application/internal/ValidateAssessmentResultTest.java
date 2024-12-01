package org.flickit.assessment.core.application.internal;

import org.flickit.assessment.common.exception.CalculateNotValidException;
import org.flickit.assessment.common.exception.ConfidenceCalculationNotValidException;
import org.flickit.assessment.common.exception.DeprecatedVersionException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadAssessmentResultPort;
import org.flickit.assessment.core.application.port.out.kitcustom.LoadKitCustomLastModificationTimePort;
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
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.resultWithValidations;
import static org.flickit.assessment.core.test.fixture.application.AssessmentResultMother.validResultWithJustAnId;
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

    @Mock
    private LoadKitCustomLastModificationTimePort loadKitCustomLastModificationTimePort;

    @Test
    void testValidate_assessmentResult_isValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(LocalDateTime.now().minusDays(1));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(anyLong())).thenReturn(LocalDateTime.now().minusDays(1));

        service.validate(assessmentId);
    }

    @Test
    void testValidate_assessmentResultNotFound() {
        UUID assessmentId = UUID.randomUUID();
        doThrow(new ResourceNotFoundException(COMMON_ASSESSMENT_RESULT_NOT_FOUND))
            .when(loadAssessmentResultPort).loadByAssessmentId(assessmentId);

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_FOUND, throwable.getMessage());

        verifyNoInteractions(loadKitLastMajorModificationTimePort);
    }

    @Test
    void testValidate_assessmentResultIsCalculationValid_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        var assessmentResult1 = AssessmentResultMother.resultWithValidations(null, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult1));

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.FALSE, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));

        throwable = assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());
    }

    @Test
    void testValidate_assessmentResultIsConfCalculationValid_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        var assessmentResult1 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, null, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult1));

        var throwable = assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.FALSE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));

        throwable = assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());
    }

    @Test
    void testValidate_assessmentResultLastCalculationTime_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        var assessmentResult1 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, null, LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult1));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(LocalDateTime.now());

        var throwable = assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        LocalDateTime lastKitMajorModificationTime = LocalDateTime.now();
        LocalDateTime lastCalculationTime = lastKitMajorModificationTime.minusDays(1);
        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, lastCalculationTime, LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(lastKitMajorModificationTime);

        throwable = assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());
    }

    @Test
    void testValidate_assessmentResultLastConfCalculationTime_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        LocalDateTime lastKitMajorModificationTime = LocalDateTime.now();
        LocalDateTime lastCalculationTime = lastKitMajorModificationTime.plusDays(1);

        AssessmentResult assessmentResult = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, lastCalculationTime, null);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(lastKitMajorModificationTime);

        var throwable = assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());

        LocalDateTime lastConfCalcTime = lastKitMajorModificationTime.minusDays(1);
        AssessmentResult assessmentResult2 = AssessmentResultMother.resultWithValidations(Boolean.TRUE, Boolean.TRUE, lastCalculationTime, lastConfCalcTime);
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult2));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(anyLong())).thenReturn(lastKitMajorModificationTime);

        throwable = assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, throwable.getMessage());
    }

    @Test
    void testValidate_assessmentResultKitVersionIsDeprecated_isNotValid() {
        UUID assessmentId = UUID.randomUUID();
        AssessmentResult assessmentResult = AssessmentResultMother.resultWithDeprecatedKitVersion(Boolean.TRUE, Boolean.TRUE, LocalDateTime.now(), LocalDateTime.now());
        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));

        var exception = assertThrows(DeprecatedVersionException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_KIT_VERSION_DEPRECATED, exception.getMessage());

        verifyNoInteractions(loadKitLastMajorModificationTimePort);
    }

    @Test
    void testValidate_kitCustomExistsAndCalculationIsBeforeKitCustomModification_isNotValid() {
        var assessmentResult = validResultWithJustAnId();
        var assessment = assessmentResult.getAssessment();
        var assessmentId = assessment.getId();

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessment.getAssessmentKit().getId()))
            .thenReturn(LocalDateTime.now().minusDays(1));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessment.getKitCustomId()))
            .thenReturn(LocalDateTime.now().plusDays(1));

        var exception = assertThrows(CalculateNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, exception.getMessage());
    }

    @Test
    void testValidate_kitCustomExistsAndConCalculationIsBeforeKitCustomModification_isNotValid() {
        var assessmentResult = resultWithValidations(true, true, LocalDateTime.now(), LocalDateTime.now().minusDays(2));
        var assessment = assessmentResult.getAssessment();
        var assessmentId = assessment.getId();

        when(loadAssessmentResultPort.loadByAssessmentId(assessmentId)).thenReturn(Optional.of(assessmentResult));
        when(loadKitLastMajorModificationTimePort.loadLastMajorModificationTime(assessment.getAssessmentKit().getId()))
            .thenReturn(LocalDateTime.now().minusDays(3));
        when(loadKitCustomLastModificationTimePort.loadLastModificationTime(assessment.getKitCustomId()))
            .thenReturn(LocalDateTime.now().minusDays(1));

        var exception = assertThrows(ConfidenceCalculationNotValidException.class, () -> service.validate(assessmentId));
        assertEquals(COMMON_ASSESSMENT_RESULT_NOT_VALID, exception.getMessage());
    }
}
