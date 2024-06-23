package org.flickit.assessment.data.jpa.kit.kittag;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface KitTagJpaRepository extends JpaRepository<KitTagJpaEntity, Long> {

    @Query("""
            SELECT t
            FROM KitTagJpaEntity t
            JOIN KitTagRelationJpaEntity r On t.id = r.tagId
            WHERE r.kitId = :kitId
        """)
    List<KitTagJpaEntity> findAllByKitId(long kitId);

    @Query("""
            SELECT t AS kitTag,
                r.kitId AS kitId
            FROM KitTagJpaEntity t
            JOIN KitTagRelationJpaEntity r ON t.id = r.tagId
            WHERE r.kitId IN :kitIds
        """)
    List<KitTagWithKitIdView> findAllByKitIdIn(@Param("kitIds") List<Long> kitIds);
}
