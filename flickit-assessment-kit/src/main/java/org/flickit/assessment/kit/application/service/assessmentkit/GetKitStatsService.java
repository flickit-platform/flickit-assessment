package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.AccessDeniedException;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitExpertGroupPort;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitStatsPort;
import org.flickit.assessment.kit.application.port.out.expertgroupaccess.CheckExpertGroupAccessPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_NOT_ALLOWED;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitStatsService implements GetKitStatsUseCase {

    private final LoadKitExpertGroupPort loadKitExpertGroupPort;
    private final CheckExpertGroupAccessPort checkExpertGroupAccessPort;
    private final LoadKitStatsPort loadKitStatsPort;

    @Override
    public Result getKitStats(Param param) {
        var expertGroupId = loadKitExpertGroupPort.loadKitExpertGroupId(param.getAssessmentKitId());
        if (!checkExpertGroupAccessPort.checkIsMember(expertGroupId, param.getCurrentUserId()))
            throw new AccessDeniedException(COMMON_CURRENT_USER_NOT_ALLOWED);

        return loadKitStatsPort.loadKitStats(param.getAssessmentKitId());
    }
}
