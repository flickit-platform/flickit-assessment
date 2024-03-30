package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroup.MembersView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
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

    @Query("""
        SELECT
        e.status as status
        FROM ExpertGroupAccessJpaEntity e
        WHERE e.expertGroupId = :expertGroupId AND e.userId = :userId
        """)
    Optional<Integer> findExpertGroupMemberStatus(@Param(value = "expertGroupId") long expertGroupId,
                                                  @Param(value = "userId") UUID userId);

    boolean existsByExpertGroupIdAndUserIdAndInviteToken(long expertGroupId, UUID userId, UUID inviteToken);

    @Modifying
    @Query("""
        UPDATE ExpertGroupAccessJpaEntity a
        SET a.status = 1,
            a.inviteToken = null
        WHERE a.inviteToken = :inviteToken
        """)
    void confirmInvitation(@Param(value = "inviteToken") UUID inviteToken);

    @Query("""
        select case
            when count(e)> 0 then true
            else false end
            from ExpertGroupAccessJpaEntity e
            WHERE
            e.inviteToken = :inviteToken AND :now <= e.inviteExpirationDate""")
    boolean existsByInviteTokenNotExpired(@Param(value = "inviteToken") UUID inviteToken,
                                          @Param(value = "now") LocalDateTime now);
}
