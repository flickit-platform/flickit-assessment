package org.flickit.assessment.core.application.service.evidence;

import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidence.AddEvidenceUseCase;
import org.flickit.assessment.core.application.port.out.assessment.CheckAssessmentExistencePort;
import org.flickit.assessment.core.application.port.out.assessment.CheckUserAssessmentAccessPort;
import org.flickit.assessment.core.application.port.out.evidence.CreateEvidencePort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddEvidenceServiceTest {

    @InjectMocks
    private AddEvidenceService service;

    @Mock
    private CreateEvidencePort createEvidencePort;

    @Mock
    private CheckAssessmentExistencePort checkAssessmentExistencePort;

    @Mock
    private CheckUserAssessmentAccessPort checkUserAssessmentAccessPort;

    @Test
    void testAddEvidence_ValidParam_PersistsAndReturnsId() {
        AddEvidenceUseCase.Param param = new AddEvidenceUseCase.Param(
            "desc",
            UUID.randomUUID(),
            1L,
            "POSITIVE",
            UUID.randomUUID()
        );
        UUID expectedId = UUID.randomUUID();
        when(checkAssessmentExistencePort.existsById(param.getAssessmentId())).thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCreatedById())).thenReturn(true);
        when(createEvidencePort.persist(any(CreateEvidencePort.Param.class))).thenReturn(expectedId);

        AddEvidenceUseCase.Result result = service.addEvidence(param);
        assertNotNull(result.id());
        assertEquals(expectedId, result.id());

        ArgumentCaptor<CreateEvidencePort.Param> createPortParam = ArgumentCaptor.forClass(CreateEvidencePort.Param.class);
        verify(createEvidencePort).persist(createPortParam.capture());

        assertEquals(param.getDescription(), createPortParam.getValue().description());
        assertEquals(param.getCreatedById(), createPortParam.getValue().createdById());
        assertEquals(param.getAssessmentId(), createPortParam.getValue().assessmentId());
        assertEquals(param.getQuestionId(), createPortParam.getValue().questionId());
        assertNotNull(createPortParam.getValue().creationTime());
        assertNotNull(createPortParam.getValue().lastModificationTime());
    }

    @Test
    void testAddEvidence_InvalidAssessmentId_ThrowNotFoundException() {
        AddEvidenceUseCase.Param param = new AddEvidenceUseCase.Param(
            "desc",
            UUID.randomUUID(),
            1L,
            "POSITIVE",
            UUID.randomUUID()
        );
        when(checkAssessmentExistencePort.existsById(param.getAssessmentId())).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> service.addEvidence(param));

        ArgumentCaptor<UUID> assessmentIdParam = ArgumentCaptor.forClass(UUID.class);
        verify(checkAssessmentExistencePort).existsById(assessmentIdParam.capture());
        assertEquals(param.getAssessmentId(), assessmentIdParam.getValue());
        verify(createEvidencePort, never()).persist(any());
    }

    @Test
    void testAddEvidence_InvalidCurrentUserId_ThrowDeniedAccessException() {
        AddEvidenceUseCase.Param param = new AddEvidenceUseCase.Param(
            "desc",
            UUID.randomUUID(),
            1L,
            "POSITIVE",
            UUID.randomUUID()
        );

        when(checkAssessmentExistencePort.existsById(param.getAssessmentId())).thenReturn(true);
        when(checkUserAssessmentAccessPort.hasAccess(param.getAssessmentId(), param.getCreatedById())).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> service.addEvidence(param));
    }
}
