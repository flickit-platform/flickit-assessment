package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.User;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.Param;

import java.time.Duration;
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
    LoadEvidenceAttachmentsPort loadEvidenceAttachmentsPort;

    @Mock
    CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Mock
    LoadEvidencePort loadEvidencePort;

    @Mock
    AssessmentAccessChecker assessmentAccessChecker;

    @Test
    @DisplayName("The evidence should exist for getting the list of added attachments.")
    void testGetEvidenceAttachmentListService_evidenceNotExists_NotFoundException() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var param = new Param(evidenceId, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenThrow(new ResourceNotFoundException(EVIDENCE_ID_NOT_FOUND));

        var throwable = assertThrows(ResourceNotFoundException.class, () -> service.getEvidenceAttachments(param));
        assertEquals(EVIDENCE_ID_NOT_FOUND, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verifyNoInteractions(loadEvidenceAttachmentsPort, assessmentAccessChecker, createFileDownloadLinkPort);
    }

    @Test
    @DisplayName("Getting list of an evidence's attachments with valid parameters should cause list of its attachments for who has access.")
    void testGetEvidenceAttachmentListService_validParam_ListEvidenceAttachment() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var evidence = EvidenceMother.simpleEvidenceWithId(evidenceId);
        var param = new Param(evidenceId, currentUserId);
        var attachment1 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des", new User(UUID.randomUUID(), "Name1"));
        var attachment2 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des", new User(UUID.randomUUID(), "Name2"));
        var attachments = List.of(attachment1, attachment2);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(eq(evidence.getAssessmentId()), eq(currentUserId), any())).thenReturn(true);
        when(loadEvidenceAttachmentsPort.loadEvidenceAttachments(evidenceId)).thenReturn(attachments);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any(Duration.class))).thenReturn("link");

        assertDoesNotThrow(() -> service.getEvidenceAttachments(param));

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verify(assessmentAccessChecker).isAuthorized(eq(evidence.getAssessmentId()), eq(currentUserId), any());
        verify(loadEvidenceAttachmentsPort).loadEvidenceAttachments(evidenceId);
        verify(createFileDownloadLinkPort, times(2)).createDownloadLink(anyString(), any(Duration.class));
    }

    @Test
    @DisplayName("Getting list of an evidence's attachments with valid parameters should cause AccessDenied for who has not access.")
    void testGetEvidenceAttachmentListService_UserHasNotAccess_AccessDeniedException() {
        var evidenceId = UUID.randomUUID();
        var currentUserId = UUID.randomUUID();
        var evidence = EvidenceMother.simpleEvidenceWithId(evidenceId);
        var param = new Param(evidenceId, currentUserId);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(eq(evidence.getAssessmentId()), eq(currentUserId), any())).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getEvidenceAttachments(param));

        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verify(assessmentAccessChecker).isAuthorized(eq(evidence.getAssessmentId()), eq(currentUserId), any());
        verifyNoInteractions(loadEvidenceAttachmentsPort, createFileDownloadLinkPort);
    }
}
