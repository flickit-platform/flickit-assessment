package org.flickit.flickitassessmentcore.application.service.answer;

import org.flickit.flickitassessmentcore.application.port.in.answer.SubmitAnswerCommand;
import org.flickit.flickitassessmentcore.application.port.out.answer.CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.LoadAnswerByAssessmentResultIdAndQuestionIdPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.SaveAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.answer.UpdateAnswerPort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.CheckAssessmentResultExistencePort;
import org.flickit.flickitassessmentcore.application.port.out.assessmentresult.InvalidateAssessmentResultPort;
import org.flickit.flickitassessmentcore.application.service.exception.ResourceNotFoundException;
import org.flickit.flickitassessmentcore.domain.Answer;
import org.flickit.flickitassessmentcore.domain.AssessmentResult;
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
    private UpdateAnswerPort updateAnswerPort;

    @Mock
    private LoadAnswerByAssessmentResultIdAndQuestionIdPort loadAnswerPort;

    @Mock
    private InvalidateAssessmentResultPort invalidateAssessmentResultPort;

    @Mock
    private CheckAssessmentResultExistencePort assessmentResultExistencePort;

    @Mock
    private CheckAnswerExistenceByAssessmentResultIdAndQuestionIdPort checkAnswerExistencePort;

    @Test
    void submitAnswer_SavesAnswerAndInvalidatesAssessmentResult_WhenAnswerNotExist() {
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            UUID.randomUUID(),
            1L,
            1L
        );
        doReturn(true).when(assessmentResultExistencePort).existsById(command.getAssessmentResultId());
        doReturn(false).when(checkAnswerExistencePort).existsByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());

        UUID savedAnswerId = UUID.randomUUID();
        doReturn(savedAnswerId).when(saveAnswerPort).persist(any(SaveAnswerPort.Param.class));

        service.submitAnswer(command);

        verify(assessmentResultExistencePort, times(1)).existsById(command.getAssessmentResultId());
        verify(saveAnswerPort, times(1)).persist(any(SaveAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(command.getAssessmentResultId());
        verifyNoInteractions(
            updateAnswerPort,
            loadAnswerPort
        );
    }

    @Test
    void submitAnswer_UpdatesAnswerAndInvalidatesAssessmentResult_WhenAnswerWithDifferentAnswerOptionExist() {
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            UUID.randomUUID(),
            1L,
            1L
        );
        UUID existAnswerId = UUID.randomUUID();
        Answer existAnswer = new Answer(
            existAnswerId,
            new AssessmentResult(),
            1L,
            2L
        );

        doReturn(true).when(assessmentResultExistencePort).existsById(command.getAssessmentResultId());
        doReturn(true).when(checkAnswerExistencePort).existsByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());
        doReturn(existAnswer).when(loadAnswerPort).loadByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());

        doReturn(existAnswerId).when(updateAnswerPort).update(any(UpdateAnswerPort.Param.class));

        service.submitAnswer(command);

        verify(assessmentResultExistencePort, times(1)).existsById(command.getAssessmentResultId());
        verify(loadAnswerPort, times(1)).loadByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());
        verify(updateAnswerPort, times(1)).update(any(UpdateAnswerPort.Param.class));
        verify(invalidateAssessmentResultPort, times(1)).invalidateById(command.getAssessmentResultId());
        verifyNoInteractions(
            saveAnswerPort
        );
    }

    @Test
    void submitAnswer_DoesntInvalidateAssessmentResult_WhenAnswerWithSameAnswerOptionExist() {
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            UUID.randomUUID(),
            1L,
            1L
        );
        UUID existAnswerId = UUID.randomUUID();
        Answer existAnswer = new Answer(
            existAnswerId,
            new AssessmentResult(),
            1L,
            1L
        );
        doReturn(true).when(assessmentResultExistencePort).existsById(command.getAssessmentResultId());
        doReturn(true).when(checkAnswerExistencePort).existsByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());
        doReturn(existAnswer).when(loadAnswerPort).loadByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());

        service.submitAnswer(command);

        verify(assessmentResultExistencePort, times(1)).existsById(command.getAssessmentResultId());
        verify(loadAnswerPort, times(1)).loadByAssessmentResultIdAndQuestionId(any(UUID.class), anyLong());
        verifyNoInteractions(
            saveAnswerPort,
            updateAnswerPort,
            invalidateAssessmentResultPort
        );
    }

    @Test
    void submitAnswer_ThrowsResourceNotFoundException_WhenAssessmentResultIdNotFound() {
        SubmitAnswerCommand command = new SubmitAnswerCommand(
            UUID.randomUUID(),
            1L,
            1L
        );
        doReturn(false).when(assessmentResultExistencePort).existsById(command.getAssessmentResultId());

        assertThrows(ResourceNotFoundException.class, () -> service.submitAnswer(command));

        verify(assessmentResultExistencePort, times(1)).existsById(command.getAssessmentResultId());
        verifyNoInteractions(
            saveAnswerPort,
            updateAnswerPort,
            loadAnswerPort,
            invalidateAssessmentResultPort
        );
    }
}
