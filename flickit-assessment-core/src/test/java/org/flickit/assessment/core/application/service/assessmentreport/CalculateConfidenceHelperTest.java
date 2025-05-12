package org.flickit.assessment.core.application.service.assessmentreport;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadConfidenceLevelCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedConfidencePort;
import org.flickit.assessment.core.test.fixture.application.AssessmentResultMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

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

    @Captor
    private ArgumentCaptor<AssessmentResult> assessmentResultCaptor;

    @Test
    void calculateConfidence_whenValidInput_thenUpdatesStateAndReturnsConfidenceValue() {
        UUID assessmentId = UUID.randomUUID();
        double expectedConfidence = 0.85;
        var assessmentResult = spy(AssessmentResultMother.resultWithValidations(true, false, LocalDateTime.now(), LocalDateTime.MIN));

        when(loadConfidenceLevelCalculateInfoPort.load(assessmentId)).thenReturn(assessmentResult);
        doReturn(expectedConfidence).when(assessmentResult).calculateConfidenceValue();

        double result = helper.calculate(assessmentId);

        verify(updateCalculatedConfidenceLevelResultPort).updateCalculatedConfidence(assessmentResultCaptor.capture());
        verify(updateAssessmentPort).updateLastModificationTime(eq(assessmentId), any(LocalDateTime.class));

        AssessmentResult capturedResult = assessmentResultCaptor.getValue();

        assertEquals(expectedConfidence, result);
        assertEquals(expectedConfidence, capturedResult.getConfidenceValue());
        assertTrue(capturedResult.getIsConfidenceValid());
        assertNotNull(capturedResult.getLastModificationTime());
        assertNotNull(capturedResult.getLastConfidenceCalculationTime());
        assertTrue(capturedResult.getLastConfidenceCalculationTime().isAfter(LocalDateTime.MIN));
    }
}
