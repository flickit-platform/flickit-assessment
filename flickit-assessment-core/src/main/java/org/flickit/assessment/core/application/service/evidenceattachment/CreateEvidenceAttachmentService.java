package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.core.application.port.in.evidenceattachment.CreateEvidenceAttachmentUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.UploadEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.evidenceattachment.SaveEvidenceAttachmentPort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateEvidenceAttachmentService implements CreateEvidenceAttachmentUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadEvidencePort loadEvidencePort;
    private final UploadEvidenceAttachmentPort uploadEvidenceAttachmentPort;
    private final SaveEvidenceAttachmentPort saveEvidenceAttachmentPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result createAttachment(Param param) {
        var evidence = loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId());
        if (!evidence.getCreatedById().equals(param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String path = uploadEvidenceAttachmentPort.uploadAttachment(param.getAttachment());

        var attachmentId = saveEvidenceAttachmentPort.saveAttachment(param.getEvidenceId(), path, param.getCurrentUserId(), LocalDateTime.now());
        var link = createFileDownloadLinkPort.createDownloadLink(path, EXPIRY_DURATION);

        return new Result(attachmentId, link);
    }
}
