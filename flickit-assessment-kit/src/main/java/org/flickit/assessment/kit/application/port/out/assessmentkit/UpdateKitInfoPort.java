package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.EditKitInfoUseCase;

public interface UpdateKitInfoPort {

    EditKitInfoUseCase.Result update(EditKitInfoUseCase.Param param);
}
