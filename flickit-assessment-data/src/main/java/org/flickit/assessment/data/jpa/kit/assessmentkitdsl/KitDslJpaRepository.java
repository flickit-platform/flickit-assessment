package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface KitDslJpaRepository extends JpaRepository<KitDslJpaEntity, Long> {

    @Modifying
    @Query("UPDATE AssessmentKitDslJpaEntity a SET " +
        "a.assessmentKitId = :kitId " +
        "WHERE a.id = :id")
    void updateById(Long id, Long kitId);
}
