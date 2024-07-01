package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.flickit.assessment.core.test.fixture.application.UserMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.Param;

import java.time.Duration;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetEvidenceAttachmentsServiceTest {

    @InjectMocks
    private GetEvidenceAttachmentsService service;

    @Mock
    LoadEvidenceAttachmentsPort loadEvidenceAttachmentsPort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Mock
    LoadUserPort loadUserPort;

    @Test
    @DisplayName("The evidence should exist for getting the list of added attachments.")
    void testGetEvidenceAttachmentListService_evidenceNotExists_NotFoundException() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, currentUserId);

        when(loadEvidenceAttachmentsPort.loadEvidenceAttachments(evidenceId)).thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getEvidenceAttachments(param));
        assertEquals(EVIDENCE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadEvidenceAttachmentsPort).loadEvidenceAttachments(evidenceId);
        verifyNoInteractions(createFileDownloadLinkPort, loadUserPort);
    }

    @Test
    @DisplayName("Getting list of an evidence's attachments with valid parameters should cause list of its attachments.")
    void testGetEvidenceAttachmentListService_validParam_ListEvidenceAttachment() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, currentUserId);
        var attachment1 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des", UUID.randomUUID());
        var attachment2 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des", UUID.randomUUID());
        var attachments = List.of(attachment1, attachment2);

        when(loadEvidenceAttachmentsPort.loadEvidenceAttachments(evidenceId)).thenReturn(attachments);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any(Duration.class))).thenReturn("link");
        when(loadUserPort.loadById(any(UUID.class))).thenReturn(Optional.of(UserMother.createUser()));

        assertDoesNotThrow(() -> service.getEvidenceAttachments(param));

        verify(loadEvidenceAttachmentsPort).loadEvidenceAttachments(evidenceId);
        verify(loadUserPort, times(2)).loadById(any(UUID.class));
        verify(createFileDownloadLinkPort, times(2)).createDownloadLink(anyString(), any(Duration.class));
    }
}
