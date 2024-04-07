package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;

public interface LoadKitStatsPort {

    GetKitStatsUseCase.Result loadKitStats(Long kitId);
}
