package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadKitDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDownloadLinkService implements GetKitDownloadLinkUseCase {

    private final LoadKitDownloadLinkPort loadKitDownloadLinkPort;

    @Override
    public String getKitLink(Param param) {
        return loadKitDownloadLinkPort.loadKitDownloadLink(param.getKitId());
    }
}
