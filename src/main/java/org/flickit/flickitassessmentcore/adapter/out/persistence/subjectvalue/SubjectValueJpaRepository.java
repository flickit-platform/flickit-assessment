package org.flickit.flickitassessmentcore.adapter.out.persistence.subjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface SubjectValueJpaRepository extends JpaRepository<SubjectValueJpaEntity, UUID> {

    List<SubjectValueJpaEntity> findByAssessmentResultId(UUID resultId);

    @Query("SELECT s AS subVal FROM SubjectValueJpaEntity s " +
        "LEFT JOIN AssessmentResultJpaEntity ar ON s.assessmentResult.id = ar.id " +
        "WHERE ar.lastModificationTime = (SELECT MAX(r.lastModificationTime) FROM AssessmentResultJpaEntity r WHERE r.id = s.assessmentResult.id) " +
        "ORDER BY ar.lastModificationTime DESC")
    List<SubjectValueJpaEntity> findBySubjectIdOrderByLastModificationTimeDesc(Long subjectId);

    @Modifying
    @Query("update SubjectValueJpaEntity a set a.maturityLevelId = :maturityLevelId where a.id = :id")
    void updateMaturityLevelById(@Param(value = "id") UUID id,
                                 @Param(value = "maturityLevelId") Long maturityLevelId);

}
