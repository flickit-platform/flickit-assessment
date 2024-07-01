package org.flickit.assessment.core.application.service.evidenceattachment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.evidenceattachment.GetEvidenceAttachmentsUseCase;
import org.flickit.assessment.core.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.flickit.assessment.core.application.port.out.evidenceattachment.LoadEvidenceAttachmentsPort;

import java.time.Duration;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetEvidenceAttachmentsService implements GetEvidenceAttachmentsUseCase {

    private final LoadEvidenceAttachmentsPort loadEvidenceAttachmentsPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    @Override
    public List<EvidenceAttachmentsItem> getEvidenceAttachments(Param param) {
        var portResult = loadEvidenceAttachmentsPort.loadEvidenceAttachments(param.getEvidenceId());
        return portResult
            .stream()
            .map(this::createLink).toList();
    }

    private EvidenceAttachmentsItem createLink(LoadEvidenceAttachmentsPort.Result evidenceAttachment) {
        return new EvidenceAttachmentsItem(evidenceAttachment.id(),
            createFileDownloadLinkPort.createDownloadLink(evidenceAttachment.file(), EXPIRY_DURATION),
            evidenceAttachment.description());
    }
}
