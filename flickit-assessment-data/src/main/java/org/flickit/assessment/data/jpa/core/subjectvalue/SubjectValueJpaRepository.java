package org.flickit.assessment.data.jpa.core.subjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubjectValueJpaRepository extends JpaRepository<SubjectValueJpaEntity, UUID> {

    List<SubjectValueJpaEntity> findByAssessmentResultId(UUID resultId);

    Optional<SubjectValueJpaEntity> findBySubjectIdAndAssessmentResult_Id(Long subjectId, UUID assessmentResultId);

    List<SubjectValueJpaEntity> findAllByIdIn(Collection<UUID> ids);

    @Query("""
            SELECT
                sv as subjectValue,
                s as subject
            FROM SubjectValueJpaEntity sv
            JOIN SubjectJpaEntity s ON sv.subjectId = s.id AND sv.assessmentResult.kitVersionId = s.kitVersionId
            WHERE sv.assessmentResult.id = :assessmentResultId
        """)
    List<SubjectValueWithSubjectView> findAllWithSubjectByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId);

    @Query("""
            SELECT
                sv as subjectValue,
                s as subject
            FROM SubjectValueJpaEntity sv
            JOIN SubjectJpaEntity s ON sv.subjectId = s.id AND sv.assessmentResult.kitVersionId = s.kitVersionId
            WHERE sv.assessmentResult.id = :assessmentResultId AND s.id IN :subjectIds
        """)
    List<SubjectValueWithSubjectView> findAllWithSubjectByAssessmentResultId(@Param("assessmentResultId") UUID assessmentResultId,
                                                                             @Param("subjectIds") Collection<Long> subjectIds);

    @Query("""
            SELECT
                sv as subjectValue,
                s as subject
            FROM SubjectValueJpaEntity sv
            JOIN SubjectJpaEntity s ON sv.subjectId = s.id AND sv.assessmentResult.kitVersionId = s.kitVersionId
            WHERE sv.assessmentResult.id = :assessmentResultId and s.id = :subjectId
        """)
    Optional<SubjectValueWithSubjectView> findBySubjectIdAndAssessmentResultId(@Param("subjectId") Long subjectId,
                                                                               @Param("assessmentResultId") UUID assessmentResultId);
}
