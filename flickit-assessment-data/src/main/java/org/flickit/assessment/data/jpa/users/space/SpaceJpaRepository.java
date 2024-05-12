package org.flickit.assessment.data.jpa.users.space;

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
}
