package org.flickit.assessment.kit.application.port.out.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;

public interface LoadKitEditableInfoPort {

    GetKitEditableInfoUseCase.KitEditableInfo loadKitEditableInfo(Long kitId);
}
