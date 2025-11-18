package org.flickit.assessment.core.adapter.out.persistence.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.space.SpaceStatus;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.core.application.domain.Space;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceListPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpaceOwnerPort;
import org.flickit.assessment.core.application.port.out.space.LoadSpacePort;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.flickit.assessment.core.adapter.out.persistence.space.SpaceMapper.mapToDomain;
import static org.flickit.assessment.core.common.ErrorMessageKey.ASSESSMENT_ID_NOT_FOUND;

@Component("coreSpacePersistenceJpaAdapter")
@RequiredArgsConstructor
public class SpacePersistenceJpaAdapter implements
    LoadSpaceOwnerPort,
    LoadSpacePort,
    LoadSpaceListPort {

    private final SpaceJpaRepository repository;

    @Override
    public UUID loadOwnerId(UUID assessmentId) {
        return repository.findOwnerByAssessmentId(assessmentId)
            .orElseThrow(() -> new ResourceNotFoundException(ASSESSMENT_ID_NOT_FOUND));
    }

    @Override
    public Optional<Space> loadById(long spaceId) {
        return repository.findByIdAndDeletedFalse(spaceId)
            .map(SpaceMapper::mapToDomain);
    }

    @Override
    public Optional<Space> loadByAssessmentId(UUID assessmentId) {
        return repository.findByAssessmentIdAndDeletedFalse(assessmentId)
            .map(SpaceMapper::mapToDomain);
    }

    @Override
    public Optional<SpaceStatus> loadStatusById(long spaceId) {
        return repository.findStatusById(spaceId)
            .map(SpaceStatus::valueOfById);
    }

    @Override
    public List<SpaceWithAssessmentCount> loadByOwnerId(UUID ownerId) {
        return repository.findByOwnerId(ownerId, SpaceStatus.ACTIVE.getId()).stream()
            .map(entity -> new SpaceWithAssessmentCount(
                mapToDomain(entity.getSpace()),
                entity.getAssessmentsCount()))
            .toList();
    }
}
