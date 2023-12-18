package org.flickit.assessment.data.jpa.kit.assessmentkit;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AssessmentKitJpaRepository extends JpaRepository<AssessmentKitJpaEntity, Long> {

    @Query("SELECT k.expertGroupId FROM AssessmentKitJpaEntity k where k.id = :id")
    Long loadExpertGroupIdById(@Param("id") Long id);
}
