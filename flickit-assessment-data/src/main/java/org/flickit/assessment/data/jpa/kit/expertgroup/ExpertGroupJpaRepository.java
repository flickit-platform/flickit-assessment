package org.flickit.assessment.data.jpa.kit.expertgroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface ExpertGroupJpaRepository extends JpaRepository<ExpertGroupJpaEntity, Long> {

    @Query("SELECT e.ownerId FROM ExpertGroupJpaEntity as e where e.id = :id")
    UUID loadOwnerIdById(@Param("id") Long id);

    @Query("""
            SELECT
                e.id as expertGroupId,
                e.name as name,
                e.picture as picture,
                e.bio as bio,
                e.ownerId as ownerId,
                COUNT(DISTINCT e.id) as publishedKitsCount,
                COUNT(DISTINCT ac.userId) as membersCount
            FROM ExpertGroupJpaEntity e
            LEFT JOIN AssessmentKitJpaEntity ak on e.id = ak.expertGroupId AND ak.published = true
            LEFT JOIN ExpertGroupAccessJpaEntity ac on e.id = ac.expertGroupId
            WHERE EXISTS (
                SELECT 1 FROM ExpertGroupAccessJpaEntity ac
                WHERE ac.expertGroupId = e.id AND ac.userId = :currentUserId
            )
            GROUP BY
                e.id,
                e.name,
                e.picture,
                e.bio,
                e.ownerId
        """)
    Page<ExpertGroupWithDetailsView> getExpertGroupSummaries(@Param(value = "currentUserId") UUID currentUserId, Pageable pageable);

    @Query("""
        SELECT
        u.displayName as displayName
        FROM ExpertGroupAccessJpaEntity e
        LEFT JOIN UserJpaEntity u on e.userId = u.id
        WHERE e.expertGroupId = :expertGroup
        ORDER BY u.displayName ASC
            LIMIT 5""")
    List<MemberView> getMembersByExpert(@Param(value = "expertGroup") Long expertGroup);
}
