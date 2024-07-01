package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.Param;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEvidenceAttachmentsServiceTest {

    @InjectMocks
    private GetEvidenceAttachmentsService service;

    @Mock
    LoadEvidencePort loadEvidencePort;

    @Mock
    LoadEvidenceAttachmentsPort loadEvidenceAttachmentsPort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    @DisplayName("The evidence should exist for getting attachment list of it.")
    void testGetEvidenceAttachmentListService_evidenceNotExists_NotFoundException() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));
        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getEvidenceAttachments(param));
        assertEquals(EVIDENCE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(loadEvidenceAttachmentsPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Getting list of an evidence's attachments should be done by the creator of it.")
    void testGetEvidenceAttachmentListService_currentUserNotAllowed_ValidationException() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, currentUserId);
        var evidence = new Evidence(evidenceId, "des", UUID.randomUUID(), UUID.randomUUID(), UUID.randomUUID(),
            0L, 1, LocalDateTime.now(), LocalDateTime.now(), false);

        when(loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId())).thenReturn(evidence);
        var throwable = assertThrows(ValidationException.class, () -> service.getEvidenceAttachments(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(loadEvidenceAttachmentsPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Getting list of an evidence's attachments with valid parameters should cause list of its attachments.")
    void testGetEvidenceAttachmentListService_validParam_ListEvidenceAttachment() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, currentUserId);
        var evidence = new Evidence(evidenceId, "des", currentUserId, UUID.randomUUID(), UUID.randomUUID(),
            0L, 1, LocalDateTime.now(), LocalDateTime.now(), false);
        var attachment1 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des");
        var attachment2 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des");
        var attachments = List.of(attachment1, attachment2);

        when(loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId())).thenReturn(evidence);
        when(loadEvidenceAttachmentsPort.loadEvidenceAttachments(evidenceId)).thenReturn(attachments);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any(Duration.class))).thenReturn("link");

        assertDoesNotThrow(() -> service.getEvidenceAttachments(param));

        verify(loadEvidencePort).loadNotDeletedEvidence(param.getEvidenceId());
        verify(loadEvidenceAttachmentsPort).loadEvidenceAttachments(evidenceId);
        verify(createFileDownloadLinkPort, times(2)).createDownloadLink(anyString(), any(Duration.class));
    }
}
