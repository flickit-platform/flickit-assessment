package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitVersionIdPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_KIT_ID_NOT_FOUND;

@Component("coreAssessmentKitPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitLastMajorModificationTimePort,
    LoadAssessmentKitVersionIdPort {

    private final AssessmentKitJpaRepository repository;

    @Override
    public LocalDateTime loadLastMajorModificationTime(Long kitId) {
        return repository.loadLastMajorModificationTime(kitId);
    }

    @Override
    public Long loadVersionId(long kitId) {
        AssessmentKitJpaEntity kitEntity = repository.findById(kitId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));
        return kitEntity.getKitVersionId();
    }
}
