package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentSpacePort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("adviceAssessmentPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    LoadAssessmentSpacePort {

    private final AssessmentJpaRepository repository;

    @Override
    public Optional<Long> loadAssessmentSpaceId(UUID assessmentId) {
        return repository.findById(assessmentId)
            .map(AssessmentJpaEntity::getSpaceId);
    }
}
