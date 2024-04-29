package org.flickit.assessment.data.jpa.users.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Query("SELECT s.ownerId FROM SpaceJpaEntity as s where s.id = :id") //TODO: add this:  and deleted=false
    Optional<UUID> loadOwnerIdById(Long id);

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
