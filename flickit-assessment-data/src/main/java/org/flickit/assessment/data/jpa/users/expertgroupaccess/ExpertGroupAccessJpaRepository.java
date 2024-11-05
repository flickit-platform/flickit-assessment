package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import org.flickit.assessment.data.jpa.users.expertgroup.ExpertGroupMembersView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
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
            AND g.deleted = FALSE
            AND ((:status = 0 AND e.inviteExpirationDate > :now) OR :status = 1)
        """)
    Page<ExpertGroupMembersView> findExpertGroupMembers(@Param(value = "expertGroupId") Long expertGroupId,
                                                        @Param(value = "status") int status,
                                                        @Param(value = "now") LocalDateTime now,
                                                        Pageable pageable);

    boolean existsByExpertGroupIdAndUserId(@Param(value = "expertGroupId") long expertGroupId,
                                           @Param(value = "userId") UUID userId);

    @Query("""
        SELECT
        e.status as status
        FROM ExpertGroupAccessJpaEntity e
        LEFT JOIN ExpertGroupJpaEntity g on g.id = e.expertGroupId
        WHERE e.expertGroupId = :expertGroupId AND e.userId = :userId AND g.deleted = FALSE
        """)
    Optional<Integer> findExpertGroupMemberStatus(@Param(value = "expertGroupId") long expertGroupId,
                                                  @Param(value = "userId") UUID userId);

    @Query("""
        SELECT a
        FROM ExpertGroupAccessJpaEntity a
        LEFT JOIN ExpertGroupJpaEntity e on a.expertGroupId = e.id
        WHERE a.expertGroupId = :expertGroupId AND a.userId = :userId AND e.deleted = FALSE
        """)
    Optional<ExpertGroupAccessJpaEntity> findByExpertGroupIdAndAndUserId(@Param(value = "expertGroupId") long expertGroupId,
                                                                         @Param(value = "userId") UUID userId);

    @Modifying
    @Query("""
        UPDATE ExpertGroupAccessJpaEntity a
        SET a.status = 1,
            a.inviteToken = null,
            a.inviteExpirationDate = null,
            a.lastModificationTime = :modificationTime
        WHERE a.expertGroupId = :expertGroupId AND a.userId = :userId
        """)
    void confirmInvitation(@Param(value = "expertGroupId") long expertGroupId,
                           @Param(value = "userId") UUID userId,
                           @Param(value = "modificationTime") LocalDateTime modificationTime);

    @Modifying
    @Query("""
            UPDATE ExpertGroupAccessJpaEntity a
            SET a.lastSeen = :currentTime
            WHERE a.expertGroupId = :expertGroupId AND a.userId = :userId
        """)
    void updateLastSeen(@Param("expertGroupId") long expertGroupId,
                        @Param("userId") UUID userId,
                        @Param("currentTime") LocalDateTime currentTime);

    @Query("""
            SELECT
                u.id as id,
                u.email as email,
                u.displayName as displayName
            FROM ExpertGroupAccessJpaEntity e
            LEFT JOIN ExpertGroupJpaEntity g on g.id = e.expertGroupId
            LEFT JOIN UserJpaEntity u on e.userId = u.id
            WHERE e.expertGroupId = :id AND g.deleted = FALSE AND e.status = 1
        """)
    List<ExpertGroupActiveMemberView> findActiveMembers(@Param("id") long id);
}
