package org.flickit.assessment.data.jpa.kit.kitlanguage;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitLanguageJpaRepository extends JpaRepository<KitLanguageJpaEntity, KitLanguageJpaEntity.EntityId> {

    List<KitLanguageJpaEntity> findAllByKitIdIn(List<Long> kitIds);
}
