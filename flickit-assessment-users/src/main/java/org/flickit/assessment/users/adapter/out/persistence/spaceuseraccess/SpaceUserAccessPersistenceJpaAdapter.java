package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.data.jpa.users.space.SpaceJpaRepository;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CheckSpaceAccessPort;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.users.common.ErrorMessageKey.SPACE_ID_NOT_FOUND;

@Component
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceJpaAdapter implements
    CreateSpaceUserAccessPort,
    CheckSpaceAccessPort {

    private final SpaceUserAccessJpaRepository repository;
    private final SpaceJpaRepository spaceRepository;

    @Override
    public void persist(CreateSpaceUserAccessPort.Param param) {
        SpaceUserAccessJpaEntity unsavedEntity = new SpaceUserAccessJpaEntity(param.spaceId(), param.userId(),
            param.createdBy(), param.creationTime());
        repository.save(unsavedEntity);
    }

    @Override
    public void persistAll(List<CreateSpaceUserAccessPort.Param> param) {
        List<SpaceUserAccessJpaEntity> entities = param
            .stream()
            .map(SpaceUserAccessMapper::paramsToEntity).toList();

        repository.saveAll(entities);
    }

    @Override
    public boolean checkIsMember(long spaceId, UUID userId) {
        if (!spaceRepository.existsById(spaceId))
            throw new ResourceNotFoundException(SPACE_ID_NOT_FOUND);

        return repository.existsByUserIdAndSpaceId(userId, spaceId);
    }
}
