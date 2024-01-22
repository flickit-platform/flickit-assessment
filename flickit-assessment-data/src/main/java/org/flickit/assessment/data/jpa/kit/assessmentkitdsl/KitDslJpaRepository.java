package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface KitDslJpaRepository extends JpaRepository<KitDslJpaEntity, Long> {

    @Modifying
    @Query("""
            UPDATE KitDslJpaEntity a SET
                a.kitId = :kitId
            WHERE a.id = :id
        """)
    void updateById(Long id, Long kitId);

    @Query("""
            SELECT a.dslPath as url
            FROM KitDslJpaEntity a
            WHERE a.kitId = :kitId
        """)
    String findDslFileByKitId(@Param("kitId") long kitId);
}
