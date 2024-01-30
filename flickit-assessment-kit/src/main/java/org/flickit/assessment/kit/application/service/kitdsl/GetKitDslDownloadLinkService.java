package org.flickit.assessment.kit.application.service.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.CreateDslDownloadLinkPort;
import org.flickit.assessment.kit.application.port.out.kitdsl.LoadDslFilePathPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;
import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_KIT_DSL_DOWNLOAD_LINK_FILE_PATH_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitDslDownloadLinkService implements GetKitDownloadLinkUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadDslFilePathPort loadDslFilePathPort;
    private final CreateDslDownloadLinkPort createDslDownloadLinkPort;

    @Override
    public String getKitDslDownloadLink(Param param) {
        var expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(param.getKitId());

        if (!checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        String filePath = loadDslFilePathPort.loadDslFilePath(param.getKitId())
            .orElseThrow(() -> new ResourceNotFoundException(GET_KIT_DSL_DOWNLOAD_LINK_FILE_PATH_NOT_FOUND));

        return createDslDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
    }
}
