package org.flickit.assessment.kit.application.service.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupPictureLinkPort;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadExpertGroupPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetExpertGroupService implements GetExpertGroupUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofHours(1);
    private final LoadExpertGroupPort loadExpertGroupPort;
    private final LoadExpertGroupPictureLinkPort loadExpertGroupPictureLinkPort;

    @Override
    public ExpertGroup getExpertGroup(Param param) {
        var portResult = loadExpertGroupPort.loadExpertGroup(toParam(param.getId()));
        var picturePath = loadExpertGroupPictureLinkPort.loadPictureLink(portResult.picture(), EXPIRY_DURATION);

        return new ExpertGroup(portResult.id(),
            portResult.title(),
            portResult.bio(),
            portResult.about(),
            picturePath,
            portResult.website(),
            portResult.ownerId().equals(param.getCurrentUserId()));
    }

    private LoadExpertGroupPort.Param toParam(long id) {
        return new LoadExpertGroupPort.Param(id);
    }
}
