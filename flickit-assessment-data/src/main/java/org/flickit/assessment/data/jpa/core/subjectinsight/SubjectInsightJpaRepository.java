package org.flickit.assessment.data.jpa.core.subjectinsight;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.UUID;

public interface SubjectInsightJpaRepository extends JpaRepository<SubjectInsightJpaEntity, SubjectInsightJpaEntity.EntityId> {

    boolean existsByAssessmentResultIdAndSubjectId(UUID assessmentResultId, Long subjectId);

    @Modifying
    @Query("""
            UPDATE SubjectInsightJpaEntity si
            SET si.insight = :insight,
                si.insightTime = :insightTime,
                si.insightBy = :insightBy
            WHERE si.assessmentResultId = :assessmentResultId AND si.subjectId = :subjectId
        """)
    void updateByAssessmentResultIdAndSubjectId(UUID assessmentResultId, Long subjectId, String insight, LocalDateTime localDateTime, UUID uuid);
}
