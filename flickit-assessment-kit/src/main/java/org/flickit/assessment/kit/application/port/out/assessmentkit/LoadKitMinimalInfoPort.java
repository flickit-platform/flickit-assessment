package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase.Result;

public interface LoadKitMinimalInfoPort {

    Result loadKitMinimalInfo(Long kitId);
}
