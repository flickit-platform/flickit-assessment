package org.flickit.assessment.users.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.ExpertGroup;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupUseCase;
import org.flickit.assessment.users.application.port.out.expertgroup.LoadExpertGroupPort;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.apache.commons.lang3.StringUtils.isBlank;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupService implements GetExpertGroupUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadExpertGroupPort loadExpertGroupPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public Result getExpertGroup(Param param) {
        ExpertGroup expertGroup = loadExpertGroupPort.loadExpertGroup(param.getId());

        String pictureLink = getPictureDownloadLink(expertGroup.getPicture());
        boolean editable = expertGroup.getOwnerId().equals(param.getCurrentUserId());
        return new Result(expertGroup, pictureLink, editable);
    }

    private String getPictureDownloadLink(String filePath) {
        if (isBlank(filePath))
            return null;
        return createFileDownloadLinkPort.createDownloadLink(filePath, EXPIRY_DURATION);
    }
}
