package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitStatsPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class GetKitStatsService implements GetKitStatsUseCase {

    private final LoadKitStatsPort loadKitStatsPort;

    @Override
    public Result getKitStats(Param param) {
        return loadKitStatsPort.loadKitStats(param.getAssessmentKitId());
    }
}
