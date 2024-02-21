package org.flickit.assessment.advice.adapter.out.persistence.assessmentresult;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessmentresult.LoadAssessmentResultIdPort;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component("adviceAssessmentResultPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentResultPersistenceJpaAdapter implements LoadAssessmentResultIdPort {

    private final AssessmentResultJpaRepository repository;

    @Override
    public Optional<UUID> loadByAssessmentId(UUID assessmentId) {
        return repository.findFirstByAssessment_IdOrderByLastModificationTimeDesc(assessmentId)
            .map(AssessmentResultJpaEntity::getId);
    }
}
