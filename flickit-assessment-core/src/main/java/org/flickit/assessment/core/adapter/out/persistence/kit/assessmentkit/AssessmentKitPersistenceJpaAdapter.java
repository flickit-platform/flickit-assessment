package org.flickit.assessment.core.adapter.out.persistence.kit.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.AssessmentKit;
import org.flickit.assessment.core.application.port.out.assessmentkit.CheckKitAccessPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadAssessmentKitPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitInfoPort;
import org.flickit.assessment.core.application.port.out.assessmentkit.LoadKitLastMajorModificationTimePort;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_KIT_ID_NOT_FOUND;

@Component("coreAssessmentKitPersistenceJpaAdapter")
@RequiredArgsConstructor
public class AssessmentKitPersistenceJpaAdapter implements
    LoadKitLastMajorModificationTimePort,
    CheckKitAccessPort,
    LoadKitInfoPort,
    LoadAssessmentKitPort {

    private final AssessmentKitJpaRepository repository;

    @Override
    public LocalDateTime loadLastMajorModificationTime(Long kitId) {
        return repository.loadLastMajorModificationTime(kitId);
    }

    @Override
    public Optional<Long> checkAccess(long kitId, UUID userId) {
        return repository.existsByUserId(kitId, userId);
    }

    @Override
    public Result loadKitInfo(long id) {
        AssessmentKitJpaEntity kitEntity = repository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_KIT_ID_NOT_FOUND));

        return new Result(kitEntity.getTitle(), kitEntity.getCreatedBy(), kitEntity.getExpertGroupId());
    }

    @Override
    public Optional<AssessmentKit> loadAssessmentKit(long kitId) {
        return repository.findById(kitId)
            .map(AssessmentKitMapper::mapToDomainModel);
    }
}
