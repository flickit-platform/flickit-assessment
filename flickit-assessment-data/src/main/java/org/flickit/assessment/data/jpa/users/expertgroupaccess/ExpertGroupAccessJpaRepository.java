package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroup.MembersView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpertGroupAccessJpaRepository extends JpaRepository<ExpertGroupAccessJpaEntity, Long> {

    boolean existsByExpertGroupIdAndUserId(@Param(value = "expertGroupId") long expertGroupId,
                                           @Param(value = "userId") UUID userId);

    @Query("""
        SELECT
        u.id as id,
        u.email as email,
        u.displayName as displayName,
        u.bio as bio,
        u.picture as picture,
        u.linkedin as linkedin
        FROM ExpertGroupAccessJpaEntity e
        LEFT JOIN UserJpaEntity u on e.userId = u.id
        WHERE e.expertGroupId = :expertGroupId
        """)
    Page<MembersView> findExpertGroupMembers(@Param(value = "expertGroupId") Long expertGroupId, Pageable pageable);

    @Modifying
    @Query("""
        UPDATE ExpertGroupAccessJpaEntity e SET
            e.deleted = TRUE
        WHERE e.expertGroupId = :expertGroupId and e.userId = :userId
        """)
    void deleteMember(@Param(value = "userId") UUID userId,
                      @Param(value = "expertGroupId") long expertGroupId);
}
