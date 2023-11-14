package org.flickit.assessment.kit.application.service;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.UpdateKitByDslUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateKitByDslService implements UpdateKitByDslUseCase {

    @Override
    public void update(Param param) {

    }
}
