package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidenceattachment.CreateEvidenceAttachmentUseCase.Param;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.UploadEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.SaveEvidenceAttachmentPort;

import static org.flickit.assessment.core.common.ErrorMessageKey.CREATE_EVIDENCE_ATTACHMENT_CURRENT_USER_NOT_ALLOWED;
import static org.junit.jupiter.api.Assertions.*;

import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.DisplayName;

@ExtendWith(MockitoExtension.class)
class CreateEvidenceAttachmentServiceTest {

    @InjectMocks
    private CreateEvidenceAttachmentService service;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private UploadEvidenceAttachmentPort uploadEvidenceAttachmentPort;

    @Mock
    private SaveEvidenceAttachmentPort saveEvidenceAttachmentPort;

    @Mock
    private CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Test
    @DisplayName("Creating an attachment for a non-exist evidence should return NotFoundException error.")
    void createEvidenceAttachment_NonExistEvidence_notFoundException() {
        var evidenceId = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, attachment, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));
        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.createAttachment(param));

        assertEquals(EVIDENCE_ID_NOT_FOUND, throwable.getMessage(), "Should return NotFoundException error");
        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Creating an attachment for an evidence should be done by the creator of the evidence.")
    void createEvidenceAttachment_UerIsNotOwnerEvidence_ValidationException() {
        var evidenceId = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var userId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, currentUserId);
        var evidence = new Evidence(evidenceId, "des", userId, userId, UUID.randomUUID(),
            0L, 1, time, time, false);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);

        var throwable = assertThrows(ValidationException.class, () -> service.createAttachment(param));
        assertEquals(CREATE_EVIDENCE_ATTACHMENT_CURRENT_USER_NOT_ALLOWED, throwable.getMessage(), "Should return ValidationException error");

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(uploadEvidenceAttachmentPort, saveEvidenceAttachmentPort, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Creating an attachment for an evidence with valid parameters should cause saving the attachment")
    void createEvidenceAttachment_ValidParameters_savingTheAttachment() {
        var evidenceId = UUID.randomUUID();
        var attachmentId = UUID.randomUUID();
        var filePath = "path/to/attachment.txt";
        var link = "http://link/to/attachment.txt/whith/expiray/date";
        var uniqueFileNme = UUID.randomUUID();
        MockMultipartFile attachment = new MockMultipartFile("attachment", "attachment.txt", "text/plain", "attachment.txt".getBytes());
        var currentUserId = UUID.randomUUID();
        var time = LocalDateTime.now();
        var param = new Param(evidenceId, attachment, currentUserId);
        var evidence = new Evidence(evidenceId, "des", currentUserId, currentUserId, uniqueFileNme,
            0L, 1, time, time, false);

        ArgumentCaptor<LocalDateTime> timeArgumentCaptor = ArgumentCaptor.forClass(LocalDateTime.class);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(uploadEvidenceAttachmentPort.uploadAttachment(attachment)).thenReturn(filePath);
        when(saveEvidenceAttachmentPort.saveAttachment(eq(evidenceId), eq(filePath), eq(currentUserId), timeArgumentCaptor.capture())).thenReturn(attachmentId);
        when(createFileDownloadLinkPort.createDownloadLink(filePath, Duration.ofDays(1))).thenReturn(link);

        var result = service.createAttachment(param);
        assertEquals(attachmentId, result.attachmentId(), "Attachment id should match");
        assertEquals(link, result.link(), "Link should match");

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verify(uploadEvidenceAttachmentPort).uploadAttachment(attachment);
        verify(createFileDownloadLinkPort).createDownloadLink(filePath, Duration.ofDays(1));
        verify(saveEvidenceAttachmentPort).saveAttachment(eq(evidenceId), eq(filePath), eq(currentUserId), timeArgumentCaptor.capture());
    }

}
