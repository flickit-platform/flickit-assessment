package org.flickit.assessment.data.jpa.kit.kitversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface KitVersionJpaRepository extends JpaRepository<KitVersionJpaEntity, Long> {

    @Modifying
    @Query("""
            UPDATE KitVersionJpaEntity k
            SET k.status = :status
            WHERE id = :id
        """)
    void updateStatus(@Param("id") long kitVersionId, @Param("status") int status);

    @Modifying
    @Query("""
            UPDATE KitVersionJpaEntity kv
            SET kv.lastModificationTime = :lastModificationTime,
                kv.lastModifiedBy = :lastModifiedBy
            WHERE kv.id = :id
        """)
    void updateModificationInfo(@Param("id") long id,
                                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                                @Param("lastModifiedBy") UUID lastModifiedBy);
}
