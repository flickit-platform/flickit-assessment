package org.flickit.assessment.data.jpa.users.space;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface SpaceJpaRepository extends JpaRepository<SpaceJpaEntity, Long> {

    @Query("SELECT s.ownerId FROM SpaceJpaEntity as s") //TODO: add this: where s.id = :id and deleted=false
    Optional<UUID> loadOwnerIdById(@Param("id") Long id);

    @Query("""
            SELECT
                COUNT(DISTINCT CASE WHEN a.spaceId = :spaceId AND a.deleted=FALSE THEN a.id ELSE NULL END)
            FROM AssessmentJpaEntity a
            WHERE a.id = :spaceId
        """)
    int countAssessments(@Param("spaceId") long spaceId);
}
