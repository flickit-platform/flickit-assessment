package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupMembersView;
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
            AND e.status = :status
        """)
    Page<ExpertGroupMembersView> findExpertGroupMembers(@Param(value = "expertGroupId") Long expertGroupId,
                                                        @Param(value = "status") int status,
                                                        Pageable pageable);

    boolean existsByExpertGroupIdAndUserId(@Param(value = "expertGroupId") long expertGroupId,
                                           @Param(value = "userId") UUID userId);

    @Query("""
        SELECT
        e.status as status
        FROM ExpertGroupAccessJpaEntity e
        WHERE e.expertGroupId = :expertGroupId AND e.userId = :userId
        """)
    Optional<Integer> findExpertGroupMemberStatus(@Param(value = "expertGroupId") long expertGroupId,
                                                  @Param(value = "userId") UUID userId);

    @Query("""
        SELECT
        a.inviteExpirationDate as inviteExpirationDate,
        a.inviteToken as inviteToken,
        a.status as status
        FROM ExpertGroupAccessJpaEntity a
        LEFT JOIN ExpertGroupJpaEntity e on a.expertGroupId = e.id
        WHERE a.expertGroupId = :expertGroupId AND a.userId = :userId AND e.deleted=FALSE
        """)
    Page<ExpertGroupAccessInvitationView> findByExpertGroupIdAndAndUserId
        (@Param(value = "expertGroupId") long expertGroupId,
         @Param(value = "userId") UUID userId,
         Pageable pageable);

    @Modifying
    @Query("""
        UPDATE ExpertGroupAccessJpaEntity a
        SET a.status = 1,
            a.inviteToken = null,
            a.lastModificationTime = :modificationTime,
            a.inviteExpirationDate = null
        WHERE a.expertGroupId = :expertGroupId AND a.userId = :userId
        """)
    void confirmInvitation(@Param(value = "expertGroupId") long expertGroupId,
                           @Param(value = "userId") UUID userId,
                           @Param(value = "modificationTime")LocalDateTime modificationTime);
}
