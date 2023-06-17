package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerCommand;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CheckAssessmentResultExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SubmitAnswerServiceTest {

    @Spy
    @InjectMocks
    private SubmitAnswerService service;

    @Mock
    private SaveAnswerPort saveAnswerPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Mock
    private CheckAssessmentResultExistencePort assessmentResultExistencePort;

    @Test
    void answerMetricCommand_SavesAnswerAndInvalidatesAssessmentResult_WhenValidCommand() {
        SubmitAnswerCommand command = createValidCommand();
        doReturn(true).when(assessmentResultExistencePort).existsById(command.getAssessmentResultId());

        UUID ExpectedAnswerId = UUID.randomUUID();
        doReturn(ExpectedAnswerId).when(saveAnswerPort).persist(any(SaveAnswerPort.Param.class));

        service.submitAnswer(command);

        verify(assessmentResultExistencePort, times(1)).existsById(command.getAssessmentResultId());
        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateAssessmentResultById(command.getAssessmentResultId());
    }

    @Test
    void answerMetricCommand_ThrowsResourceNotFoundException_WhenAssessmentResultIdNotFound() {
        SubmitAnswerCommand command = createValidCommand();
        doReturn(false).when(assessmentResultExistencePort).existsById(command.getAssessmentResultId());

        assertThrows(ResourceNotFoundException.class, () -> service.submitAnswer(command));

        verify(assessmentResultExistencePort, times(1)).existsById(command.getAssessmentResultId());
        verifyNoInteractions(
            saveAnswerPort,
            invalidateAssessmentResultPort
        );
    }

    private static SubmitAnswerCommand createValidCommand() {
        UUID assessmentResultId = UUID.randomUUID();
        Long questionId = 1L;
        Long answerOptionId = 1L;
        return new SubmitAnswerCommand(assessmentResultId, questionId, answerOptionId);
    }
}
