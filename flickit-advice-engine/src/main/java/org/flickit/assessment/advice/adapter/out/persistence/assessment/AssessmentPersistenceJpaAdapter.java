package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.LoadAssessmentSpacePort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedAttributeIdsRelatedToAssessmentPort;
import org.flickit.assessment.advice.application.port.out.assessment.LoadSelectedLevelIdsRelatedToAssessmentPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaEntity;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

@Component("adviceAssessmentPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    LoadAssessmentSpacePort,
    LoadSelectedAttributeIdsRelatedToAssessmentPort,
    LoadSelectedLevelIdsRelatedToAssessmentPort {

    private final AssessmentJpaRepository repository;

    @Override
    public Optional<Long> loadAssessmentSpaceId(UUID assessmentId) {
        return repository.findById(assessmentId)
            .map(AssessmentJpaEntity::getSpaceId);
    }

    @Override
    public Set<Long> loadSelectedAttributeIdsRelatedToAssessment(UUID assessmentId, Set<Long> attributeIds) {
        return repository.findSelectedAttributeIdsRelatedToAssessment(assessmentId, attributeIds);
    }

    @Override
    public Set<Long> loadSelectedLevelIdsRelatedToAssessment(UUID assessmentId, Set<Long> levelIds) {
        return repository.findSelectedLevelIdsRelatedToAssessment(assessmentId, levelIds);
    }
}
