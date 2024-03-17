package org.flickit.assessment.data.jpa.kit.kittag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface KitTagJpaRepository extends JpaRepository<KitTagJpaEntity, Long> {

    @Query("""
            SELECT tag
            FROM KitTagJpaEntity tag
            JOIN KitTagRelationJpaEntity kittag ON kittag.tagId = tag.id AND kittag.kitId = :kitId
        """)
    List<KitTagJpaEntity> findAllByKitId(Long kitId);
}
