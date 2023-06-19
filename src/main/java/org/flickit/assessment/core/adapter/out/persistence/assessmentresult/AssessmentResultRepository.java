package org.flickit.assessment.core.adapter.out.persistence.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

    @Query("select a from AssessmentResultJpaEntity a " +
        "where a.assessment.id = :assessmentId")
    Set<AssessmentResultJpaEntity> findAssessmentResultByAssessmentId(@Param("assessmentId") UUID assessmentId);
}
