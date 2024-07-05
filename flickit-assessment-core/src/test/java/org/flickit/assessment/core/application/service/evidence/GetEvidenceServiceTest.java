package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase.Param;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.GET_EVIDENCE_ID_NOT_NULL;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEvidenceServiceTest {

    @InjectMocks
    private GetEvidenceService service;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    @DisplayName("For loading an evidence, the evidence should be exist or throw notFoundException.")
    void testLoadEvidence_evidenceNotExist_NotFoundException() {
        var id = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);

        when(loadEvidencePort.loadEvidenceWithDetails(id)).thenThrow(new ResourceNotFoundException(GET_EVIDENCE_ID_NOT_NULL));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getEvidence(param));

        assertEquals(GET_EVIDENCE_ID_NOT_NULL, throwable.getMessage());

        verify(loadEvidencePort).loadEvidenceWithDetails(id);
        verifyNoInteractions(checkUserAssessmentAccessPort);
    }

    @Test
    @DisplayName("For loading an evidence, the current user should have access to the corresponding kit.")
    void testLoadEvidence_AccessDoesNotExist_AccessDeniedException() {
        var id = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);
        var assessmentId = UUID.randomUUID();
        var portResult = new LoadEvidencePort.Result(id, "des", assessmentId,
            new LoadEvidencePort.Questionnaire(0L, "title"),
            new LoadEvidencePort.Question(1L, "title", 1),
            new LoadEvidencePort.Answer(
                new LoadEvidencePort.AnswerOption(2L, "title", 2),
                1,
                true), "DisplayName", LocalDateTime.now(), LocalDateTime.now());

        when(loadEvidencePort.loadEvidenceWithDetails(id)).thenReturn(portResult);
        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getEvidence(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadEvidencePort).loadEvidenceWithDetails(id);
        verify(checkUserAssessmentAccessPort).hasAccess(assessmentId, currentUserId);
    }

    @Test
    @DisplayName("Loading an evidence with valid parameters should returns a valid evidence.")
    void testLoadEvidence_ValidParameters_ReturnsValidEvidence() {
        var id = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(id, currentUserId);
        var assessmentId = UUID.randomUUID();
        var portResult = new LoadEvidencePort.Result(id, "des", assessmentId,
            new LoadEvidencePort.Questionnaire(0L, "title"),
            new LoadEvidencePort.Question(1L, "title", 1),
            new LoadEvidencePort.Answer(
                new LoadEvidencePort.AnswerOption(2L, "title", 2),
                1,
                true), "DisplayName", LocalDateTime.now(), LocalDateTime.now());

        when(loadEvidencePort.loadEvidenceWithDetails(id)).thenReturn(portResult);
        when(checkUserAssessmentAccessPort.hasAccess(assessmentId, currentUserId)).thenReturn(true);

        var result = assertDoesNotThrow(() -> service.getEvidence(param));

        assertEquals(id, result.id());
        assertEquals(portResult.description(), result.description());
        assertEquals(portResult.questionnaire().id(), result.questionnaire().id());
        assertEquals(portResult.questionnaire().title(), result.questionnaire().title());
        assertEquals(portResult.question().id(), result.question().id());
        assertEquals(portResult.question().title(), result.question().title());
        assertEquals(portResult.question().index(), result.question().index());
        assertEquals(portResult.answer().answerOption().id(), result.answer().answerOption().id());
        assertEquals(portResult.answer().answerOption().title(), result.answer().answerOption().title());
        assertEquals(portResult.answer().answerOption().index(), result.answer().answerOption().index());
        assertEquals(portResult.answer().confidenceLevel(), result.answer().confidenceLevel().id());

        verify(loadEvidencePort).loadEvidenceWithDetails(id);
        verify(checkUserAssessmentAccessPort).hasAccess(assessmentId, currentUserId);
    }
}
