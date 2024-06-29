package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ValidationException;
import org.flickit.assessment.core.application.domain.EvidenceAttachment;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentListUseCase;
import org.flickit.assessment.core.application.port.out.evidence.LoadEvidencePort;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentListPort;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceAttachmentListService implements GetEvidenceAttachmentListUseCase {

    private final LoadEvidencePort loadEvidencePort;
    private final LoadEvidenceAttachmentListPort loadEvidenceAttachmentListPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Override
    public List<EvidenceAttachment> getEvidenceAttachmentList(Param param) {
        var evidence = loadEvidencePort.loadNotDeletedEvidence(param.getEvidenceId());

        if (!evidence.getCreatedById().equals(param.getCurrentUserId()))
            throw new ValidationException(COMMON_CURRENT_USER_NOT_ALLOWED);

        var portResult = loadEvidenceAttachmentListPort.loadEvidenceAttachmentList(param.getEvidenceId());
        return portResult
            .stream()
            .map(this::addPictureLink).toList();
    }

    private EvidenceAttachment addPictureLink(LoadEvidenceAttachmentListPort.Result evidenceAttachment) {
        return new EvidenceAttachment(evidenceAttachment.id(),
            evidenceAttachment.evidenceId(),
            createFileDownloadLinkPort.createDownloadLink(evidenceAttachment.file(), EXPIRY_DURATION),
            evidenceAttachment.description());
    }
}
