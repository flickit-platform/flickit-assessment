package org.flickit.assessment.users.adapter.out.persistence.spaceuseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.users.spaceuseraccess.SpaceUserAccessJpaRepository;
import org.flickit.assessment.users.application.port.out.spaceuseraccess.CreateSpaceUserAccessPort;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SpaceUserAccessPersistenceAdapter implements
    CreateSpaceUserAccessPort {

    private final SpaceUserAccessJpaRepository repository;

    @Override
    public void createAccess(List<Param> param) {
        List<SpaceUserAccessJpaEntity> entities = param
            .stream()
            .map(SpaceUserAccessMapper::paramsToEntity).toList();

        repository.saveAll(entities);
    }
}
