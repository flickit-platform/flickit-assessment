package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.AssessmentAttrLevelExistencePort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AssessmentAttrLevelExistenceJpaAdapter implements AssessmentAttrLevelExistencePort {

    private final AssessmentJpaRepository repository;
    @Override
    public boolean exists(UUID assessmentId, Long attributeId, Long maturityLevelId) {
        return repository.existsByAttributeIdAndMaturityLevelId(assessmentId, attributeId, maturityLevelId);
    }
}
