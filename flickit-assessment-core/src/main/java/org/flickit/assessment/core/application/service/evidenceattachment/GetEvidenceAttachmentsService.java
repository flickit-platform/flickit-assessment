package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.core.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;

import java.time.Duration;
import java.util.List;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceAttachmentsService implements GetEvidenceAttachmentsUseCase {

    private final LoadUserPort loadUserPort;
    private final LoadEvidenceAttachmentsPort loadEvidenceAttachmentsPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Override
    public List<EvidenceAttachmentsItem> getEvidenceAttachments(Param param) {
        var portResult = loadEvidenceAttachmentsPort.loadEvidenceAttachments(param.getEvidenceId());
        return portResult
            .stream()
            .map(this::mapToEvidenceAttachmentsItem).toList();
    }

    private EvidenceAttachmentsItem mapToEvidenceAttachmentsItem(LoadEvidenceAttachmentsPort.Result evidenceAttachment) {
        var user = loadUserPort.loadById(evidenceAttachment.createdBy()).orElseThrow(() -> new ResourceNotFoundException(COMMON_CURRENT_USER_NOT_FOUND));
        return new EvidenceAttachmentsItem(evidenceAttachment.id(),
            createFileDownloadLinkPort.createDownloadLink(evidenceAttachment.file(), EXPIRY_DURATION),
            evidenceAttachment.description(), user.getDisplayName());
    }
}
