package org.flickit.assessment.core.application.service.assessment;

import org.assertj.core.api.Assertions;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.assessment.GetAssessmentProgressUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.GetAssessmentProgressPort;
import org.flickit.assessment.core.test.fixture.application.AssessmentMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetAssessmentProgressServiceTest {

    @InjectMocks
    private GetAssessmentProgressService service;

    @Mock
    private GetAssessmentProgressPort getAssessmentProgressPort;

    @Test
    void testGetAssessmentProgress_ValidResult() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        Param param = new Param(assessmentId);

        when(getAssessmentProgressPort.getAssessmentProgressById(assessmentId))
            .thenReturn(new GetAssessmentProgressPort.Result(assessmentId, 5));

        var result = service.getAssessmentProgress(param);

        ArgumentCaptor<UUID> answerPortAssessmentId = ArgumentCaptor.forClass(UUID.class);
        verify(getAssessmentProgressPort).getAssessmentProgressById(answerPortAssessmentId.capture());

        assertEquals(assessmentId, answerPortAssessmentId.getValue());
        verify(getAssessmentProgressPort, times(1)).getAssessmentProgressById(any());

        assertEquals(assessmentId, result.id());
        assertEquals(5, result.allAnswersCount());
    }

    @Test
    void testGetAssessmentProgress_InValidAssessmentId() {
        var assessment = AssessmentMother.assessment();
        UUID assessmentId = assessment.getId();
        Param param = new Param(assessmentId);

        when(getAssessmentProgressPort.getAssessmentProgressById(assessmentId))
            .thenThrow(new ResourceNotFoundException(GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getAssessmentProgress(param));
        Assertions.assertThat(throwable).hasMessage(GET_ASSESSMENT_PROGRESS_ASSESSMENT_RESULT_NOT_FOUND);
    }
}
