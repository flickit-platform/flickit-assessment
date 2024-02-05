package org.flickit.assessment.advice.adapter.out.persistence.assessment;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.advice.application.port.out.assessment.UserAssessmentAccessibilityPort;
import org.flickit.assessment.data.jpa.core.assessment.AssessmentJpaRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component("adviceAssessmentPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentPersistenceJpaAdapter implements
    UserAssessmentAccessibilityPort {

    private final AssessmentJpaRepository repository;

    @Override
    public boolean hasAccess(UUID assessmentId, UUID userId) {
        return repository.checkUserAccess(assessmentId, userId).isPresent();
    }
}
