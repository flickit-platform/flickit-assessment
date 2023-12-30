package org.flickit.assessment.data.jpa.kit.expertgroup;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface ExpertGroupJpaRepository extends JpaRepository<ExpertGroupJpaEntity, Long> {

    @Query("SELECT e.ownerId FROM ExpertGroupJpaEntity as e where e.id = :id")
    UUID loadOwnerIdById(@Param("id") Long id);

    // 2) Next, I utilized it in the repository. Note: The query has been tested.

    @Query("""
        SELECT
            e.id as id,
            e.name as name,
            e.about as about,
            e.picture as picture,
            e.website as website,
            e.bio as bio,
            e.ownerId as ownerId,
             COUNT(ak) as publishedKitsCount
        FROM ExpertGroupJpaEntity e
        LEFT JOIN AssessmentKitJpaEntity ak ON e.id = ak.expertGroupId AND ak.isActive = true
        GROUP BY e.id""")
    Page<ExpertGroupWithAssessmentKitCountView> getExpertGroupSummaries(Pageable pageable);
}
