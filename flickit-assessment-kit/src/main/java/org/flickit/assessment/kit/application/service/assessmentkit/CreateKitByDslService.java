package org.flickit.assessment.kit.application.service.assessmentkit;

import org.flickit.assessment.kit.application.port.in.assessmentkit.CreateKitByDslUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateKitByDslService implements CreateKitByDslUseCase {

    @Override
    public Long create(Param param) {
        return null;
    }
}
