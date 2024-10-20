package org.flickit.assessment.kit.application.service.kitversion;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.kitversion.GetKitVersionUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetKitVersionService implements GetKitVersionUseCase {

    @Override
    public Result getKitVersion(Param param) {
        return null;
    }
}
