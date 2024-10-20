package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase;
import org.flickit.assessment.kit.application.port.out.expertgroup.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.flickit.assessment.kit.application.port.out.kitversion.LoadKitVersionPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitVersionService implements GetKitVersionUseCase {

    private final LoadKitVersionPort loadKitVersionPort;
    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;

    @Override
    public Result getKitVersion(Param param) {
        var kitVersion = loadKitVersionPort.load(param.getKitVersionId());
        var expertGroup = loadKitExpertGroupPort.loadKitExpertGroup(kitVersion.getKit().getId());

        if (!checkExpertGroupAccessPort.checkIsMember(expertGroup.getId(), param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return toResult(kitVersion, expertGroup);
    }

    private Result toResult(KitVersion kitVersion, ExpertGroup expertGroup) {
        var assessmentKit = kitVersion.getKit();
        return new Result(kitVersion.getId(),
            kitVersion.getCreationTime(),
            new Result.AssessmentKit(assessmentKit.getId(), assessmentKit.getTitle(), new Result.ExpertGroup(expertGroup.getId(), expertGroup.getTitle()))
        );
    }
}
