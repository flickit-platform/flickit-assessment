package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkitdsl.LoadDslFilePathPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDslDownloadLinkService implements GetKitDownloadLinkUseCase {

    private final LoadDslFilePathPort loadDslFilePathPort;
    private final Duration EXPIRY_DURATION = Duration.ofHours(1);

    @SneakyThrows
    @Override
    public String getKitLink(Param param) {
        return loadDslFilePathPort.loadDslFilePath(param.getKitId(), EXPIRY_DURATION);
    }
}
