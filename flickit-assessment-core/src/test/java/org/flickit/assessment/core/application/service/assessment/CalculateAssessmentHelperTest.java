package org.flickit.assessment.core.application.service.assessment;

import org.flickit.assessment.core.application.domain.AssessmentResult;
import org.flickit.assessment.core.application.port.out.assessment.UpdateAssessmentPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.LoadCalculateInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentresult.UpdateCalculatedResultPort;
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
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CalculateAssessmentHelperTest {

    @InjectMocks
    private CalculateAssessmentHelper helper;

    @Mock
    private LoadCalculateInfoPort loadCalculateInfoPort;

    @Mock
    private UpdateCalculatedResultPort updateCalculatedResultPort;

    @Mock
    private UpdateAssessmentPort updateAssessmentPort;

    @Captor
    private ArgumentCaptor<AssessmentResult> assessmentResultCaptor;

    @Test
    void testCalculateAssessmentHelper_whenValidInput_thenCalculateAssessmentAndReturnsMaturityLevel() {
        UUID assessmentId = UUID.randomUUID();
        var assessmentResult = AssessmentResultMother.resultWithValidations(false, true, LocalDateTime.MIN, LocalDateTime.now());

        when(loadCalculateInfoPort.load(assessmentId)).thenReturn(assessmentResult);

        var result = helper.calculateMaturityLevel(assessmentId);
        verify(updateCalculatedResultPort).updateCalculatedResult(assessmentResultCaptor.capture());
        verify(updateAssessmentPort).updateLastModificationTime(assessmentId, assessmentResult.getLastModificationTime());

        assertNotNull(result);
        assertEquals(assessmentResultCaptor.getValue().getMaturityLevel(), result);
        assertTrue(assessmentResultCaptor.getValue().getIsCalculateValid());
        assertNotNull(assessmentResultCaptor.getValue().getMaturityLevel());
        assertTrue(assessmentResultCaptor.getValue().getLastCalculationTime().isAfter(LocalDateTime.MIN));
        assertNotNull(assessmentResultCaptor.getValue().getLastModificationTime());
    }
}
