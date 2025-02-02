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

    @Query("""
            SELECT si
            FROM SubjectInsightJpaEntity si
            JOIN AssessmentResultJpaEntity ar ON si.assessmentResultId = ar.id
            RIGHT JOIN SubjectJpaEntity s ON si.subjectId  = s.id AND ar.kitVersionId = s.kitVersionId
            WHERE ar.id = :assessmentResultId
        """)
    List<SubjectInsightJpaEntity> findByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE SubjectInsightJpaEntity si
            SET si.insight = :insight,
                si.insightTime = :insightTime,
                si.lastModificationTime = :lastModificationTime,
                si.insightBy = :insightBy,
                si.approved = :isApproved
            WHERE si.assessmentResultId = :assessmentResultId AND si.subjectId = :subjectId
        """)
    void updateByAssessmentResultIdAndSubjectId(@Param("assessmentResultId") UUID assessmentResultId,
                                                @Param("subjectId") Long subjectId,
                                                @Param("insight") String insight,
                                                @Param("insightTime") LocalDateTime insightTime,
                                                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                                                @Param("insightBy") UUID insightBy,
                                                @Param("isApproved") boolean isApproved);

    @Modifying
    @Query("""
            UPDATE SubjectInsightJpaEntity si
            SET si.approved = true,
                si.lastModificationTime = :lastModificationTime
            WHERE si.assessmentResultId = :assessmentResultId AND si.subjectId = :subjectId
        """)
    void approve(@Param("assessmentResultId") UUID assessmentResultId,
                 @Param("subjectId") Long subjectId,
                 @Param("lastModificationTime") LocalDateTime lastModificationTime);
}
