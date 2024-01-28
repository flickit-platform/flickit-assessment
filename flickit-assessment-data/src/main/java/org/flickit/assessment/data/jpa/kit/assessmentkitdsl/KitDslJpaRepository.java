package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface KitDslJpaRepository extends JpaRepository<KitDslJpaEntity, Long> {

    @Modifying
    @Query("""
            UPDATE KitDslJpaEntity a SET
                a.kitId = :kitId
            WHERE a.id = :id
        """)
    void updateById(Long id, Long kitId);

    @Query("""
            SELECT kd.dslPath as url
            FROM KitDslJpaEntity kd
            LEFT JOIN AssessmentKitJpaEntity ak on ak.Id = kd.kitId
            WHERE kd.kitId = :kitId
        """)
    Optional<String> findDslPathByKitId(@Param("kitId") long kitId);
}
