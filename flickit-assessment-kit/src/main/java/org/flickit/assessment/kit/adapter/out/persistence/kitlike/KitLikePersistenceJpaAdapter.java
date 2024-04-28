package org.flickit.assessment.kit.adapter.out.persistence.kitlike;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.data.jpa.kit.kitlike.KitLikeJpaRepository;
import org.flickit.assessment.kit.application.port.out.kitlike.CheckKitLikeExistencePort;
import org.flickit.assessment.kit.application.port.out.kitlike.CountKitLikePort;
import org.flickit.assessment.kit.application.port.out.kitlike.CreateKitLikePort;
import org.flickit.assessment.kit.application.port.out.kitlike.DeleteKitLikePort;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class KitLikePersistenceJpaAdapter implements
    CheckKitLikeExistencePort,
    CreateKitLikePort,
    DeleteKitLikePort,
    CountKitLikePort {

    private final KitLikeJpaRepository repository;

    @Override
    public boolean exist(Long kitId, UUID userId) {
        return repository.existsByKitIdAndUserId(kitId, userId);
    }

    @Override
    public void create(Long kitId, UUID userId) {
        repository.save(KitLikeMapper.toJpaEntity(kitId, userId));
    }

    @Override
    public void delete(Long kitId, UUID userId) {
        repository.deleteByKitIdAndUserId(kitId, userId);
    }

    @Override
    public int countByKitId(long kitId) {
        return repository.countByKitId(kitId);
    }
}
