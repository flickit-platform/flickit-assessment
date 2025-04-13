package org.flickit.assessment.data.jpa.kit.kitbanner;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface KitBannerJpaRepository extends JpaRepository<KitBannerJpaEntity, UUID> {
}
