package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface KitDslJpaRepository extends JpaRepository<KitDslJpaEntity, Long> {

    @Modifying
    @Query("""
            UPDATE KitDslJpaEntity a
            SET a.kitId = :kitId,
                a.lastModifiedBy = :lastModifiedBy,
                a.lastModificationTime = :lastModificationTime
            WHERE a.id = :id
        """)
    void updateById(@Param("id") Long id,
                    @Param("kitId") Long kitId,
                    @Param("lastModifiedBy") UUID lastModifiedBy,
                    @Param("lastModificationTime") LocalDateTime lastModificationTime);

    @Modifying
    @Query("""
            UPDATE KitDslJpaEntity a
            SET a.kitId = null,
                a.lastModifiedBy = :lastModifiedBy,
                a.lastModificationTime = :lastModificationTime
            WHERE a.kitId = :kitId
        """)
    void removeKitId(@Param("kitId") long kitId,
                     @Param("lastModifiedBy") UUID lastModifiedBy,
                     @Param("lastModificationTime") LocalDateTime lastModificationTime);

    @Query("""
            SELECT a.dslPath as url
            FROM KitDslJpaEntity a
            WHERE a.kitId = :kitId
        """)
    Optional<String> findDslPathByKitId(@Param("kitId") long kitId);
}
