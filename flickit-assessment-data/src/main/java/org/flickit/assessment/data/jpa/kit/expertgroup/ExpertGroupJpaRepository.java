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
        e.id as id,
        e.name as name,
        e.picture as picture,
        e.bio as bio,
        e.ownerId as ownerId,
        COUNT(ak) as publishedKitsCount,
        COUNT(ac) as membersCount
    FROM ExpertGroupJpaEntity e
    LEFT JOIN AssessmentKitJpaEntity ak ON e.id = ak.expertGroupId AND ak.isActive = true
    LEFT JOIN ExpertGroupAccessJpaEntity ac ON ac.expertGroupId = e.id
    WHERE ac.userId = :currentUserId
    GROUP BY e.id
""")

    Page<ExpertGroupWithDetailsView> getExpertGroupSummaries(Pageable pageable,
                                                             @Param(value = "currentUserId") UUID currentUseId);

    @Query("""
        SELECT
        u.displayName as displayName
        FROM ExpertGroupAccessJpaEntity e
        LEFT JOIN UserJpaEntity u on e.userId = u.id
        WHERE e.expertGroupId = :expertGroup""")
    List<MemberView> getMembersByExpert(@Param(value = "expertGroup") Long expertGroup);

}
