package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase.Param;
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

import java.time.Duration;
import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.VIEW_EVIDENCE_ATTACHMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.core.common.ErrorMessageKey.EVIDENCE_ID_NOT_FOUND;
import static org.flickit.assessment.core.test.fixture.application.UserMother.createUser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
        var attachment1 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des", createUser());
        var attachment2 = new LoadEvidenceAttachmentsPort.Result(UUID.randomUUID(), "path/to/file", "des", createUser());
        var attachments = List.of(attachment1, attachment2);

        when(loadEvidencePort.loadNotDeletedEvidence(evidenceId)).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, VIEW_EVIDENCE_ATTACHMENT)).thenReturn(true);
        when(loadEvidenceAttachmentsPort.loadEvidenceAttachments(evidenceId)).thenReturn(attachments);
        when(createFileDownloadLinkPort.createDownloadLink(anyString(), any(Duration.class))).thenReturn("link");

        var result = service.getEvidenceAttachments(param);

        assertEquals(2, result.size());
        assertEquals(attachment1.id(), result.get(0).id());
        assertEquals(attachment1.description(), result.get(0).description());
        assertEquals(attachment1.createdBy().getId(), result.get(0).createdBy().getId());
        assertEquals(attachment1.createdBy().getDisplayName(), result.get(0).createdBy().getDisplayName());
        assertEquals("link", result.get(0).link());
        assertEquals(attachment2.id(), result.get(1).id());
        assertEquals(attachment2.description(), result.get(1).description());
        assertEquals(attachment2.createdBy().getId(), result.get(1).createdBy().getId());
        assertEquals(attachment2.createdBy().getDisplayName(), result.get(1).createdBy().getDisplayName());
        assertEquals("link", result.get(1).link());

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
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, VIEW_EVIDENCE_ATTACHMENT)).thenReturn(false);

        var throwable = assertThrows(AccessDeniedException.class, () -> service.getEvidenceAttachments(param));
        assertEquals(COMMON_CURRENT_USER_NOT_ALLOWED, throwable.getMessage());

        verify(loadEvidencePort).loadNotDeletedEvidence(evidenceId);
        verify(assessmentAccessChecker).isAuthorized(eq(evidence.getAssessmentId()), eq(currentUserId), any());
        verifyNoInteractions(loadEvidenceAttachmentsPort, createFileDownloadLinkPort);
    }
}
