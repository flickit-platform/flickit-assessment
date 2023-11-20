package org.flickit.assessment.kit.adapter.out.persistence.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.flickit.assessment.kit.application.domain.AssessmentKit;
import org.flickit.assessment.kit.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements LoadAssessmentKitPort {

    private final AssessmentKitJpaRepository repository;

    @Override
    public Optional<AssessmentKit> load(Long kitId) {
        var entity = repository.findById(kitId);
        return entity.map(AssessmentKitMapper::mapToDomainModel);
    }
}
