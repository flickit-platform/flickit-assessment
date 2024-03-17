package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitEditableInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitEditableInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitEditableInfoService implements GetKitEditableInfoUseCase {

    private final LoadKitEditableInfoPort loadKitEditableInfoPort;

    @Override
    public KitEditableInfo getKitEditableInfo(Param param) {
        return loadKitEditableInfoPort.loadKitEditableInfo(param.getAssessmentKitId());
    }
}
