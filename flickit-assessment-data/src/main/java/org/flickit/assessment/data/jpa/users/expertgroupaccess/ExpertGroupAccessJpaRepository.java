package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupMembersView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ExpertGroupAccessJpaRepository extends JpaRepository<ExpertGroupAccessJpaEntity, Long> {

    @Query("""
        SELECT
        u.id as id,
        u.email as email,
        u.displayName as displayName,
        u.bio as bio,
        u.picture as picture,
        u.linkedin as linkedin,
        e.status as status,
        e.inviteExpirationDate as inviteExpirationDate
        FROM ExpertGroupAccessJpaEntity e
        LEFT JOIN ExpertGroupJpaEntity g on g.id = e.expertGroupId
        LEFT JOIN UserJpaEntity u on e.userId = u.id
        WHERE e.expertGroupId = :expertGroupId
            AND e.status = :status AND e.deleted = FALSE
        """)
    Page<ExpertGroupMembersView> findExpertGroupMembers(@Param(value = "expertGroupId") Long expertGroupId,
                                                        @Param(value = "status") int status,
                                                        Pageable pageable);

    boolean existsByExpertGroupIdAndUserIdAndDeletedFalse(@Param(value = "expertGroupId") long expertGroupId,
                                                          @Param(value = "userId") UUID userId);

    @Query("""
        SELECT
        e.status as status
        FROM ExpertGroupAccessJpaEntity e
        WHERE e.expertGroupId = :expertGroupId AND e.userId = :userId AND e.deleted = FALSE
        """)
    Optional<Integer> findExpertGroupMemberStatus(@Param(value = "expertGroupId") long expertGroupId,
                                        @Param(value = "userId") UUID userId);

    @Modifying
    @Query("""
        UPDATE ExpertGroupAccessJpaEntity e SET
            e.deleted = TRUE
        WHERE e.expertGroupId = :expertGroupId and e.userId = :userId
        """)
    void deleteMember(@Param(value = "userId") UUID userId,
                      @Param(value = "expertGroupId") long expertGroupId);
}
