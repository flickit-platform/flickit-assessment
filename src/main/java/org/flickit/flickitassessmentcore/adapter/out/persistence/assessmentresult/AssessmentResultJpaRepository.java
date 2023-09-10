package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentResultJpaRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET a.isValid = false WHERE a.id = :id")
    void invalidateById(@Param(value = "id") UUID id);

    Optional<AssessmentResultJpaEntity> findFirstByAssessment_IdOrderByLastModificationTimeDesc(UUID assessmentId);

    @Modifying
    @Query("UPDATE AssessmentResultJpaEntity a SET " +
        "a.maturityLevelId = :maturityLevelId, " +
        "a.isValid = :isValid, " +
        "a.lastModificationTime = :lastModificationTime " +
        "WHERE a.id = :id")
    void updateAfterCalculate(@Param(value = "id") UUID id,
                              @Param(value = "maturityLevelId") Long maturityLevelId,
                              @Param(value = "isValid") boolean isValid,
                              @Param(value = "lastModificationTime") LocalDateTime lastModificationTime);

    @Query("SELECT ar AS result FROM AssessmentResultJpaEntity ar " +
        "LEFT JOIN AssessmentJpaEntity a ON ar.assessment = a.id " +
        "WHERE ar.id IN (SELECT s.assessmentResult.id FROM SubjectValueJpaEntity s WHERE s.assessmentResult.id = ar.id AND s.id=:subjectId) " +
        "AND ar.lastModificationTime = (SELECT MAX(r.lastModificationTime) FROM AssessmentResultJpaEntity r WHERE r.assessment.id = a.id) " +
        "ORDER BY ar.lastModificationTime DESC")
    AssessmentResultJpaEntity findFirstBySubjectValueId(@Param("subjectId") UUID subjectId);
}
