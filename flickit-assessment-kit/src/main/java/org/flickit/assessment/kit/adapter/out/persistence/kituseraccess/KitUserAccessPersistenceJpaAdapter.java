package org.flickit.assessment.kit.adapter.out.persistence.kituseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaRepository;
import org.flickit.assessment.kit.application.domain.KitUser;
import org.flickit.assessment.kit.application.port.out.kituseraccess.LoadKitUserAccessPort;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KitUserAccessPersistenceJpaAdapter implements LoadKitUserAccessPort {

    private final KitUserAccessJpaRepository repository;

    @Override
    public Optional<KitUser> loadByKitIdAndUserEmail(Long kitId, UUID userId) {
        return repository.findById(new KitUserAccessJpaEntity.KitUserAccessKey(kitId, userId))
            .map(KitUserAccessMapper::mapToDomainModel);
    }
}
