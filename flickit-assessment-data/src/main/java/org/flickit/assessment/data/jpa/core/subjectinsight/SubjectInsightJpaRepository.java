package org.flickit.assessment.data.jpa.core.subjectinsight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectInsightJpaRepository extends JpaRepository<SubjectInsightJpaEntity, SubjectInsightJpaEntity.EntityId> {

    Optional<SubjectInsightJpaEntity> findByAssessmentResultIdAndSubjectId(UUID assessmentResultId, Long subjectId);

    boolean existsByAssessmentResultIdAndSubjectId(UUID assessmentResultId, long subjectId);

    List<SubjectInsightJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE SubjectInsightJpaEntity si
            SET si.insight = :insight,
                si.insightTime = :insightTime,
                si.insightBy = :insightBy,
                si.approved = :approved
            WHERE si.assessmentResultId = :assessmentResultId AND si.subjectId = :subjectId
        """)
    void updateByAssessmentResultIdAndSubjectId(@Param("assessmentResultId") UUID assessmentResultId,
                                                @Param("subjectId") Long subjectId,
                                                @Param("insight") String insight,
                                                @Param("insightTime") LocalDateTime insightTime,
                                                @Param("insightBy") UUID insightBy,
                                                @Param("approved") boolean approved);

    @Modifying
    @Query("""
            UPDATE SubjectInsightJpaEntity si
            SET si.approved = true
            WHERE si.assessmentResultId = :assessmentResultId AND si.subjectId = :subjectId
        """)
    void approveSubjectInsight(@Param("assessmentResultId") UUID assessmentResultId,
                               @Param("subjectId") Long subjectId);
}
