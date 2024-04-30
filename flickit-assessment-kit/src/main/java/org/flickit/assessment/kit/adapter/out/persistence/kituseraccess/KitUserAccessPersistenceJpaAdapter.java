package org.flickit.assessment.kit.adapter.out.persistence.kituseraccess;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaEntity;
import org.flickit.assessment.data.jpa.kit.kituseraccess.KitUserAccessJpaRepository;
import org.flickit.assessment.kit.application.port.out.kituseraccess.CheckKitUserAccessPort;
import org.flickit.assessment.kit.application.port.out.kituseraccess.GrantUserAccessToKitPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KitUserAccessPersistenceJpaAdapter implements
    GrantUserAccessToKitPort,
    CheckKitUserAccessPort {

    private final KitUserAccessJpaRepository repository;

    @Override
    public void grantUserAccess(Long kitId, UUID userId) {
        KitUserAccessJpaEntity entity = new KitUserAccessJpaEntity(kitId, userId);
        repository.save(entity);
    }

    @Override
    public void grantUsersAccess(Long kitId, List<UUID> userIds) {
        List<KitUserAccessJpaEntity> entities = userIds.stream()
            .map(userId -> new KitUserAccessJpaEntity(kitId, userId))
            .toList();
        repository.saveAll(entities);
    }

    @Override
    public boolean hasAccess(Long kitId, UUID userId) {
        return repository.existsById(new KitUserAccessJpaEntity.KitUserAccessKey(kitId, userId));
    }
}
