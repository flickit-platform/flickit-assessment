package org.flickit.assessment.kit.application.service.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitMinimalInfoUseCase;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadKitMinimalInfoPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitMinimalInfoService implements GetKitMinimalInfoUseCase {

    private final LoadKitMinimalInfoPort loadKitMinimalInfoPort;

    @Override
    public Result getKitMinimalInfo(Param param) {
        return loadKitMinimalInfoPort.loadKitMinimalInfo(param.getKitId());
    }
}
