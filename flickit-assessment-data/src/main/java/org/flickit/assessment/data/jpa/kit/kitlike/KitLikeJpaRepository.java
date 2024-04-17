package org.flickit.assessment.data.jpa.kit.kitlike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface KitLikeJpaRepository extends JpaRepository<KitLikeJpaEntity, KitLikeJpaEntity.KitLikeKey> {

    Optional<KitLikeJpaEntity> findByKitIdAndUserId(Long kitId, UUID userId);

    void deleteByKitIdAndUserId(Long kitId, UUID userId);

    Integer countAllByKitId(Long kitId);
}
