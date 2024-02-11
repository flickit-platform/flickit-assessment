package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component("coreAssessmentKitPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitLastMajorModificationTimePort {

    private final AssessmentKitJpaRepository repository;

    @Override
    public LocalDateTime loadLastMajorModificationTime(Long kitId) {
        return repository.loadLastMajorModificationTime(kitId);
    }
}
