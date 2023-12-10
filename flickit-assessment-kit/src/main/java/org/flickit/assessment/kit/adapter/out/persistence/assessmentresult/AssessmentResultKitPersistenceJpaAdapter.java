package org.flickit.assessment.kit.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.flickit.assessment.kit.application.port.out.assessmentresult.InvalidateAssessmentResultByKitPort;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AssessmentResultKitPersistenceJpaAdapter implements InvalidateAssessmentResultByKitPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public void invalidateByKitId(Long kitId) {
        repository.invalidateByKitId(kitId);
    }
}
