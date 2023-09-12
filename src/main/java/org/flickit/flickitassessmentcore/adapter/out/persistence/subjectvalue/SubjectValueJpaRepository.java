package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectValueJpaRepository extends JpaRepository<SubjectValueJpaEntity, UUID> {

    List<SubjectValueJpaEntity> findByAssessmentResultId(UUID resultId);

    @Query("SELECT s AS subVal FROM SubjectValueJpaEntity s " +
        "WHERE s.assessmentResult.id = :resultId AND s.subjectId = :subjectId")
    Optional<SubjectValueJpaEntity> findBySubjectIdOrderByLastModificationTimeDesc(Long subjectId, UUID resultId);

    @Modifying
    @Query("update SubjectValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

}
