package org.flickit.assessment.data.jpa.kit.kittagrelation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface KitTagRelationJpaRepository extends JpaRepository<KitTagRelationJpaEntity, Long> {

    List<KitTagRelationJpaEntity> findAllByKitId(Long kitId);
}
