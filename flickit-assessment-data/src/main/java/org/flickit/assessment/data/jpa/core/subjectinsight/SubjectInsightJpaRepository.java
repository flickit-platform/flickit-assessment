package org.flickit.assessment.data.jpa.core.subjectinsight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface SubjectInsightJpaRepository extends JpaRepository<SubjectInsightJpaEntity, SubjectInsightJpaEntity.EntityId> {

    Optional<SubjectInsightJpaEntity> findByAssessmentResultIdAndSubjectId(UUID assessmentResultId, Long subjectId);

    @Modifying
    @Query("""
            UPDATE SubjectInsightJpaEntity si
            SET si.insight = :insight,
                si.insightTime = :insightTime,
                si.insightBy = :insightBy
            WHERE si.assessmentResultId = :assessmentResultId AND si.subjectId = :subjectId
        """)
    void updateByAssessmentResultIdAndSubjectId(@Param("assessmentResultId") UUID assessmentResultId,
                                                @Param("subjectId") Long subjectId,
                                                @Param("insight") String insight,
                                                @Param("insightTime") LocalDateTime insightTime,
                                                @Param("insightBy") UUID insightBy);
}
