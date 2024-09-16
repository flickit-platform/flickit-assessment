package org.flickit.assessment.data.jpa.core.assessmentanalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AssessmentAnalysisJpaRepository extends JpaRepository<AssessmentAnalysisJpaEntity, UUID> {

    Optional<AssessmentAnalysisJpaEntity> findByAssessmentResultIdAndType(UUID assessmentResultId, int type);

    @Modifying
    @Query("""
            UPDATE AssessmentAnalysisJpaEntity a
            SET a.inputPath = :inputPath
            WHERE a.id = :id
        """)
    void updateInputPath(@Param(value = "id") UUID id, @Param(value = "inputPath") String inputPath);

    @Modifying
    @Query("""
        UPDATE AssessmentAnalysisJpaEntity AS a SET
            a.aiAnalysis = :aiAnalysis,
            a.aiAnalysisTime = :aiAnalysisTime
        WHERE a.id = :id
        """)
    void updateAiAnalysis(@Param("id") UUID id,
                @Param("aiAnalysis") String aiAnalysis,
                @Param("aiAnalysisTime") LocalDateTime aiAnalysisTime);
}
