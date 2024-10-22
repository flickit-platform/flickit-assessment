package org.flickit.assessment.core.application.service.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.in.assessment.MigrateAssessmentResultKitVersionUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor
public class MigrateAssessmentResultKitVersionService implements MigrateAssessmentResultKitVersionUseCase {

    @Override
    public void migrateKitVersion(Param param) {

    }
}
