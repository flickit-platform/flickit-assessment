package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

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

    @Query("""
            SELECT COALESCE(COUNT(kd.dslPath), 0) > 0 as urlExists
            FROM KitDslJpaEntity kd
            LEFT JOIN AssessmentKitJpaEntity ak on ak.Id = kd.kitId
            LEFT JOIN ExpertGroupJpaEntity eg on ak.expertGroupId = eg.id
            LEFT JOIN ExpertGroupAccessJpaEntity ega on ega.expertGroupId = eg.id
            WHERE kd.kitId = :kitId AND ega.userId = :userId
        """)
    Boolean checkIsMemberByKitId(@Param(value = "kitId") Long kitId,
                                 @Param(value = "userId") UUID userId);
}
