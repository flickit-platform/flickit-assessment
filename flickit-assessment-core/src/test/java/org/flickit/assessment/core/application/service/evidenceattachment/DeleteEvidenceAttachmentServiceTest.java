package org.flickit.assessment.core.application.service.evidenceattachment;

import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.application.domain.assessment.AssessmentPermission;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidenceattachment.DeleteEvidenceAttachmentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.DeleteEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentFilePathPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteEvidenceAttachmentFilePort;
import org.flickit.assessment.core.test.fixture.application.EvidenceMother;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeleteEvidenceAttachmentServiceTest {

    @InjectMocks
    private DeleteEvidenceAttachmentService deleteEvidenceAttachmentService;

    @Mock
    private DeleteEvidenceAttachmentFilePort deleteEvidenceAttachmentFilePort;

    @Mock
    private DeleteEvidenceAttachmentPort deleteEvidenceAttachmentPort;

    @Mock
    private LoadEvidencePort loadEvidencePort;

    @Mock
    private AssessmentAccessChecker assessmentAccessChecker;

    @Mock
    private LoadEvidenceAttachmentFilePathPort loadEvidenceAttachmentFilePathPort;

    private UUID evidenceId;
    private UUID attachmentId;
    private UUID currentUserId;
    private UUID assessmentId;
    private Evidence evidence;
    private DeleteEvidenceAttachmentUseCase.Param param;

    @BeforeEach
    void setUp() {
        evidence = EvidenceMother.simpleEvidence();
        evidenceId = evidence.getId();
        assessmentId = evidence.getAssessmentId();
        attachmentId = UUID.randomUUID();
        currentUserId = UUID.randomUUID();
        param = new DeleteEvidenceAttachmentUseCase.Param(evidenceId, attachmentId, currentUserId);
        when(loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId())).thenReturn(evidence);
    }

    @Test
    void testDeleteEvidenceAttachment_WhenCurrentUserDoesntHaveDeleteEvidenceAttachmentPermission_ThenThrowAccessDeniedException() {
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.DELETE_EVIDENCE_ATTACHMENT)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> deleteEvidenceAttachmentService.deleteEvidenceAttachment(param));
    }

    @Test
    void testDeleteEvidenceAttachment_WhenCurrentUserHasDeleteEvidenceAttachmentPermission_ThenAttachmentDeleted() {
        String filePath = "media/attachment.pdf";
        when(assessmentAccessChecker.isAuthorized(assessmentId, currentUserId, AssessmentPermission.DELETE_EVIDENCE_ATTACHMENT)).thenReturn(true);
        when(loadEvidenceAttachmentFilePathPort.loadEvidenceAttachmentFilePath(attachmentId)).thenReturn(filePath);

        deleteEvidenceAttachmentService.deleteEvidenceAttachment(param);

        verify(deleteEvidenceAttachmentFilePort).deleteEvidenceAttachmentFile(filePath);
        verify(deleteEvidenceAttachmentPort).deleteEvidenceAttachment(attachmentId);
    }
}
