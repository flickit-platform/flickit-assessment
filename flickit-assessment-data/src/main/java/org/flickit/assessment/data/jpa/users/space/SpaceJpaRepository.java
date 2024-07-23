package org.flickit.assessment.data.jpa.users.space;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Query("""
            SELECT s.ownerId
            FROM SpaceJpaEntity as s
            WHERE s.id = :id AND s.deleted = FALSE
        """)
    Optional<UUID> loadOwnerIdById(@Param("id") long id);

    @Query("""
            SELECT sp.ownerId
            FROM AssessmentJpaEntity asm
            JOIN SpaceJpaEntity sp ON asm.spaceId = sp.id
            WHERE asm.id = :assessmentId
        """)
    Optional<UUID> findOwnerByAssessmentId(@Param("assessmentId") UUID assessmentId);

    @Query("""
            SELECT
                s as space,
                COUNT(DISTINCT sua.userId) as membersCount,
                COUNT(DISTINCT a.id) as assessmentsCount
            FROM SpaceJpaEntity s
            LEFT JOIN AssessmentJpaEntity a on s.id = a.spaceId AND a.deleted = FALSE
            LEFT JOIN SpaceUserAccessJpaEntity sua on s.id = sua.spaceId
            WHERE s.id = :spaceId AND s.deleted = FALSE
            GROUP BY s.id
        """)
    Optional<SpaceWithDetails> loadSpaceDetails(@Param("spaceId") long id);

    @Modifying
    @Query("""
            UPDATE SpaceUserAccessJpaEntity s SET
                s.lastSeen = :currentTime
            WHERE s.spaceId = :spaceId AND s.userId = :userId
        """)
    void updateLastSeen(@Param("spaceId") long spaceId,
                        @Param("userId") UUID userId,
                        @Param("currentTime") LocalDateTime currentTime);

    @Query("""
            SELECT
                s as space,
                u.displayName as ownerName,
                COUNT(DISTINCT sua.userId) as membersCount,
                COUNT(DISTINCT (CASE WHEN fa.deleted = FALSE THEN fa.id ELSE NULL END)) as assessmentsCount,
                MAX(sua.lastSeen) as lastSeen
            FROM SpaceJpaEntity s
            LEFT JOIN AssessmentJpaEntity fa on s.id = fa.spaceId
            LEFT JOIN UserJpaEntity u ON s.ownerId = u.id
            LEFT JOIN SpaceUserAccessJpaEntity sua on s.id = sua.spaceId
            WHERE s.deleted = FALSE
                AND EXISTS (
                    SELECT 1 FROM SpaceUserAccessJpaEntity sua
                    WHERE sua.spaceId = s.id AND sua.userId = :userId
            )
            GROUP BY s.id, u.displayName
            ORDER BY lastSeen DESC
        """)
    Page<SpaceWithDetails> findByUserId(@Param(value = "userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT
                COUNT(DISTINCT a.id)
            FROM AssessmentJpaEntity a
            WHERE a.spaceId = :spaceId AND a.deleted=FALSE
        """)
    int countAssessments(@Param("spaceId") long spaceId);

    @Modifying
    @Query("""
            UPDATE SpaceJpaEntity e
            SET e.deleted = true
            WHERE e.id = :spaceId
        """)
    void delete(@Param("spaceId") long spaceId);

    boolean existsByIdAndDeletedFalse(long id);

    @Modifying
    @Query("""
            UPDATE SpaceJpaEntity s SET
                s.title = :title,
                s.code = :code,
                s.lastModificationTime = :lastModificationTime,
                s.lastModifiedBy = :lastModifiedBy
            WHERE s.id = :id
        """)
    void update(@Param("id") long id,
                @Param("title") String title,
                @Param("code") String code,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);
}
