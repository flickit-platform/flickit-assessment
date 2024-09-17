package org.flickit.assessment.data.jpa.core.assessmentanalysis;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface AssessmentAnalysisJpaRepository extends JpaRepository<AssessmentAnalysisJpaEntity, UUID> {

    Optional<AssessmentAnalysisJpaEntity> findByAssessmentResultIdAndType(UUID assessmentResultId, Integer type);

    @Modifying
    @Query("""
            UPDATE AssessmentAnalysisJpaEntity a
            SET a.inputPath = :inputPath
            WHERE a.id = :id
        """)
    void updateInputPath(@Param(value = "id") UUID id, @Param(value = "inputPath") String inputPath);
}
