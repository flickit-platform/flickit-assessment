package org.flickit.assessment.data.jpa.users.expertgroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpertGroupJpaRepository extends JpaRepository<ExpertGroupJpaEntity, Long> {

    @Query("SELECT e.ownerId FROM ExpertGroupJpaEntity as e where e.id = :id")
    Optional<UUID> loadOwnerIdById(@Param("id") Long id);

    @Query("""
            SELECT
                e.id as id,
                e.title as title,
                e.picture as picture,
                e.bio as bio,
                e.ownerId as ownerId,
                COUNT(DISTINCT CASE WHEN ak.published = true THEN ak.id ELSE NULL END) as publishedKitsCount,
                COUNT(DISTINCT ac.userId) as membersCount
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity ak on e.id = ak.expertGroupId
            LEFT JOIN ExpertGroupAccessJpaEntity ac on e.id = ac.expertGroupId
            WHERE EXISTS (
                SELECT 1 FROM ExpertGroupAccessJpaEntity ac
                WHERE ac.expertGroupId = e.id AND ac.userId = :userId
            )
            GROUP BY
                e.id,
                e.title,
                e.picture,
                e.bio,
                e.ownerId
        """)
    Page<ExpertGroupWithDetailsView> findByUserId(@Param(value = "userId") UUID userId, Pageable pageable);

    @Query("""
        SELECT
        u.displayName as displayName
        FROM ExpertGroupAccessJpaEntity e
        LEFT JOIN UserJpaEntity u on e.userId = u.id
        WHERE e.userId is not null and e.expertGroupId = :expertGroupId
        """)
    List<String> findMembersByExpertGroupId(@Param(value = "expertGroupId") Long expertGroupId, Pageable pageable);

    @Query("""
        SELECT
        e.userId as userId
        FROM ExpertGroupAccessJpaEntity e
        WHERE e.expertGroupId = :expertGroupId and e.userId is not null
        """)
    List<UUID> findMemberIdsByExpertGroupId(@Param(value = "expertGroupId") Long expertGroupId);

    @Query("""
        SELECT CASE WHEN COUNT(a) > 0 THEN true ELSE false END
        FROM AssessmentKitJpaEntity a
        WHERE a.expertGroupId = :expertGroupId
        """)
    boolean checkHavingKit(@Param("expertGroupId") Long expertGroupId);

    @Modifying
    @Query("""
        UPDATE ExpertGroupJpaEntity e
        SET e.deleted = true
        WHERE e.id = :expertGroupId
        """)
    void setAsDeleted(@Param("expertGroupId") Long expertGroupId);
}
