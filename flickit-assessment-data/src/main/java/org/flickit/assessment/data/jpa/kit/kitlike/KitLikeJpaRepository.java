package org.flickit.assessment.data.jpa.kit.kitlike;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KitLikeJpaRepository extends JpaRepository<KitLikeJpaEntity, KitLikeJpaEntity.KitLikeKey> {

    boolean existsByKitIdAndUserId(Long kitId, UUID userId);

    void deleteByKitIdAndUserId(Long kitId, UUID userId);

    int countByKitId(Long kitId);
}
