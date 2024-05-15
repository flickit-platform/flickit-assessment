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
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Query("""
            SELECT s.ownerId
            FROM SpaceJpaEntity as s
            WHERE s.id = :id
        """) //TODO: add this:  and deleted=false
    Optional<UUID> loadOwnerIdById(@Param("id") long id);

    @Query("""
            SELECT
                s as space,
                COUNT(DISTINCT sua.userId) as membersCount,
                COUNT(DISTINCT a.id) as assessmentsCount
            FROM SpaceJpaEntity s
            LEFT JOIN AssessmentJpaEntity a on s.id = a.spaceId
            LEFT JOIN SpaceUserAccessJpaEntity sua on s.id = sua.spaceId
            WHERE s.id = :spaceId AND a.deleted = FALSE
            GROUP BY s.id
        """)
    Optional<SpaceWithCounters> loadSpaceDetails(@Param("spaceId") long id);

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
                COUNT(DISTINCT sua.userId) as membersCount,
                COUNT(DISTINCT fa.id) as assessmentsCount,
                MAX(sua.lastSeen) as lastSeen
            FROM SpaceJpaEntity s
            LEFT JOIN AssessmentJpaEntity fa on s.id = fa.spaceId
            LEFT JOIN SpaceUserAccessJpaEntity sua on s.id = sua.spaceId
            WHERE EXISTS (
                SELECT 1 FROM SpaceUserAccessJpaEntity sua
                WHERE sua.spaceId = s.id AND sua.userId = :userId
            )
            GROUP BY s.id
            ORDER BY lastSeen DESC
        """)
    Page<SpaceWithCounters> findByUserId(@Param(value = "userId") UUID userId, Pageable pageable);

    @Query("""
            SELECT
                COUNT(DISTINCT CASE WHEN a.spaceId = :spaceId AND a.deleted=FALSE THEN a.id ELSE NULL END)
            FROM AssessmentJpaEntity a
            WHERE a.spaceId = :spaceId
        """)
    int countAssessments(long spaceId);

    @Modifying
    @Query("""
        UPDATE SpaceJpaEntity e
        SET e.deleted = true,
            e.deletionTime = :deletionTime
        WHERE e.id = :spaceId
        """)
    void delete(long spaceId, long deletionTime);

    boolean existsByIdAndDeletedFalse(long id);
}
