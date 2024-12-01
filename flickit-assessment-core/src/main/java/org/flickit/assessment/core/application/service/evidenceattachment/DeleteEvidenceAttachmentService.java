package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.assessment.AssessmentAccessChecker;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.domain.Evidence;
import org.flickit.assessment.core.application.port.in.evidenceattachment.DeleteEvidenceAttachmentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.DeleteEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentFilePathPort;
import org.flickit.assessment.core.application.port.out.minio.DeleteFilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.application.domain.assessment.AssessmentPermission.DELETE_EVIDENCE_ATTACHMENT;
import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class DeleteEvidenceAttachmentService implements DeleteEvidenceAttachmentUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final AssessmentAccessChecker assessmentAccessChecker;
    private final LoadEvidenceAttachmentFilePathPort loadEvidenceAttachmentFilePathPort;
    private final DeleteFilePort deleteFilePort;
    private final DeleteEvidenceAttachmentPort deleteEvidenceAttachmentPort;

    @Override
    public void deleteEvidenceAttachment(Param param) {
        Evidence evidence = loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId());
        if (!assessmentAccessChecker.isAuthorized(evidence.getAssessmentId(), param.getCurrentUserId(), DELETE_EVIDENCE_ATTACHMENT))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String filePath = loadEvidenceAttachmentFilePathPort.loadEvidenceAttachmentFilePath(param.getAttachmentId());
        deleteFilePort.deleteFile(filePath);
        deleteEvidenceAttachmentPort.deleteEvidenceAttachment(param.getAttachmentId());
    }
}
