package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface AssessmentResultJpaRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET a.isValid = false WHERE a.id = :id")
    void invalidateById(@Param(value = "id") UUID id);

    Set<AssessmentResultJpaEntity> findByAssessmentId(UUID assessmentId);
}
