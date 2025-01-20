package org.flickit.assessment.data.jpa.core.assessmentreport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.UUID;

public interface AssessmentReportJpaRepository extends JpaRepository<AssessmentReportJpaEntity, UUID> {

    @Query("""
            SELECT r.metadata
            FROM AssessmentReportJpaEntity r
            JOIN AssessmentResultJpaEntity u on r.assessmentResultId = u.id
            WHERE u.assessment.id = :assessmentId
     """)
    String findMetadataByAssessmentId(@Param("assessmentId") UUID assessmentId);
}
