package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidenceattachment.DeleteEvidenceAttachmentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.DeleteEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentFilePathPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteFilePort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteEvidenceAttachmentServiceTest {

    @InjectMocks
    private DeleteEvidenceAttachmentService deleteEvidenceAttachmentService;

    @Mock
    private DeleteFilePort deleteFilePort;

    @Mock
    private DeleteEvidenceAttachmentPort deleteEvidenceAttachmentPort;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadEvidenceAttachmentFilePathPort loadEvidenceAttachmentFilePathPort;

    @Test
    void testDeleteEvidenceAttachment_WhenCurrentUserDoesNotHaveTheRequiredPermission_ThenThrowAccessDeniedException() {
        Evidence evidence = EvidenceMother.simpleEvidence();
        UUID attachmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        var param = new DeleteEvidenceAttachmentUseCase.Param(evidence.getId(), attachmentId, currentUserId);
        when(loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId,
            AssessmentPermission.DELETE_EVIDENCE_ATTACHMENT)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> deleteEvidenceAttachmentService.deleteEvidenceAttachment(param));
    }

    @Test
    void testDeleteEvidenceAttachment_WhenCurrentUserHasTheRequiredPermission_ThenAttachmentDeleted() {
        Evidence evidence = EvidenceMother.simpleEvidence();
        UUID attachmentId = UUID.randomUUID();
        UUID currentUserId = UUID.randomUUID();

        var param = new DeleteEvidenceAttachmentUseCase.Param(evidence.getId(), attachmentId, currentUserId);

        String filePath = "media/attachment.pdf";
        when(loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId())).thenReturn(evidence);
        when(assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), currentUserId, AssessmentPermission.DELETE_EVIDENCE_ATTACHMENT)).thenReturn(true);
        when(loadEvidenceAttachmentFilePathPort.loadEvidenceAttachmentFilePath(attachmentId)).thenReturn(filePath);

        deleteEvidenceAttachmentService.deleteEvidenceAttachment(param);

        verify(deleteFilePort).deleteFile(filePath);
        verify(deleteEvidenceAttachmentPort).deleteEvidenceAttachment(attachmentId);
    }
}
