package org.flickit.assessment.data.jpa.core.subjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectValueJpaRepository extends JpaRepository<SubjectValueJpaEntity, UUID> {

    List<SubjectValueJpaEntity> findByAssessmentResultId(UUID resultId);

    Optional<SubjectValueJpaEntity> findBySubjectIdAndAssessmentResult_Id(Long subjectId, UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE SubjectValueJpaEntity a SET a.maturityLevelId = :maturityLevelId
            WHERE a.id = :id
        """)
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("""
            UPDATE SubjectValueJpaEntity a SET a.confidenceValue = :confidenceValue
            WHERE a.id = :id
        """)
    void updateConfidenceValueById(@Param(value = "id") UUID id,
                                   @Param(value = "confidenceValue") Double confidenceValue);

    @Query("""
            SELECT sv FROM SubjectValueJpaEntity sv
                JOIN SubjectJpaEntity s ON sv.subjectId = s.id AND sv.assessmentResult.kitVersionId = s.kitVersionId
            WHERE sv.assessmentResult.id = :assessmentResultId
        """)
    List<SubjectValueJpaEntity> findAllWithSubjectByAssessmentResultId(UUID assessmentResultId);
}
