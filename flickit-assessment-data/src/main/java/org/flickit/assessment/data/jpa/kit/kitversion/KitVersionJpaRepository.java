package org.flickit.assessment.data.jpa.kit.kitversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KitVersionJpaRepository extends JpaRepository<KitVersionJpaEntity, Long> {

    boolean existsByKitIdAndStatus(long kitId, int status);

    @Modifying
    @Query("""
            UPDATE KitVersionJpaEntity k
            SET k.status = :status
            WHERE k.id = :id
        """)
    void updateStatus(@Param("id") long kitVersionId, @Param("status") int status);
}
