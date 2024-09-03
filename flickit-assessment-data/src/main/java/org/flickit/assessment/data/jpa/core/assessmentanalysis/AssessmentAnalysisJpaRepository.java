package org.flickit.assessment.data.jpa.core.assessmentanalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentAnalysisJpaRepository extends JpaRepository<AssessmentAnalysisJpaEntity, UUID> {

    Optional<AssessmentAnalysisJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
        UPDATE AssessmentAnalysisJpaEntity AS a SET
            a.aiAnalysis = :aiAnalysis,
            a.aiAnalysisTime = :aiAnalysisTime
        WHERE a.id = :id
        """)
    void update(@Param("id") UUID id,
                @Param("aiAnalysis") String aiAnalysis,
                @Param("aiAnalysisTime") LocalDateTime aiAnalysisTime);

    Optional<AssessmentAnalysisJpaEntity> findByAssessmentResultIdAndType(UUID assessmentResultId, int type);
}
