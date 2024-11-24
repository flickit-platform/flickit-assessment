package org.flickit.assessment.data.jpa.kit.kitversion;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Set;

public interface KitVersionJpaRepository extends JpaRepository<KitVersionJpaEntity, Long> {

    boolean existsByKitIdAndStatus(long kitId, int status);

    List<KitVersionJpaEntity> findAllByIdIn(Set<Long> ids);

    @Modifying
    @Query("""
            UPDATE KitVersionJpaEntity k
            SET k.status = :status,
                k.statusVersion = :statusVersion
            WHERE k.id = :id
        """)
    void updateStatus(@Param("id") long kitVersionId,
                      @Param("status") int status,
                      @Param("statusVersion") Long statusVersion);
}
