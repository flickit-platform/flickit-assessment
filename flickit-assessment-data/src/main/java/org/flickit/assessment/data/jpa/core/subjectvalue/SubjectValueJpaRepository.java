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
    @Query("update SubjectValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

    @Modifying
    @Query("update SubjectValueJpaEntity a set a.confidenceValue = :confidenceValue where a.id = :id")
    void updateConfidenceValuelById(@Param(value = "id") UUID id,
                                    @Param(value = "confidenceValue") Double confidenceValue);

}
