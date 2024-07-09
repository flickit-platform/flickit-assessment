package org.flickit.assessment.data.jpa.core.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    Page<EvidenceJpaEntity> findByQuestionIdAndAssessmentIdAndDeletedFalseOrderByLastModificationTimeDesc(Long questionId,
                                                                                                          UUID assessmentId,
                                                                                                          Pageable pageable);

    Optional<EvidenceJpaEntity> findByIdAndDeletedFalse(UUID id);

    @Modifying
    @Query("""
            UPDATE EvidenceJpaEntity e
            SET e.description = :description,
                e.type = :type,
                e.lastModificationTime = :lastModificationTime,
                e.lastModifiedBy = :lastModifiedBy
            WHERE e.id = :id
        """)
    void update(@Param(value = "id") UUID id,
                @Param(value = "description") String description,
                @Param(value = "type") Integer type,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE EvidenceJpaEntity e
            SET e.deleted = true
            WHERE e.id = :id
        """)
    void delete(@Param(value = "id") UUID id);

    @Query("""
            SELECT evd.description
            FROM QuestionJpaEntity q
            LEFT JOIN EvidenceJpaEntity evd ON q.id = evd.questionId
            WHERE evd.assessmentId = :assessmentId
                AND evd.type = :type
                AND evd.deleted = false
                AND q.id IN (SELECT qs.id
                             FROM QuestionJpaEntity qs
                             LEFT JOIN AssessmentResultJpaEntity ar ON qs.kitVersionId = ar.kitVersionId
                             LEFT JOIN QuestionImpactJpaEntity qi ON qs.id = qi.questionId
                             WHERE qi.attributeId = :attributeId AND ar.assessment.id = :assessmentId)
            ORDER BY evd.lastModificationTime DESC
        """)
    Page<String> findAssessmentAttributeEvidencesByTypeOrderByLastModificationTimeDesc(@Param(value = "assessmentId") UUID assessmentId,
                                                                                       @Param(value = "attributeId") Long attributeId,
                                                                                       @Param(value = "type") Integer type,
                                                                                       Pageable pageable);

    boolean existsByIdAndDeletedFalse(UUID evidenceId);

    @Query("""
        SELECT evd.id as id,
        evd.description as description,
        evd.creationTime as creationTime,
        evd.lastModificationTime as lastModificationTime,
        u.displayName as createdBy,
        q as question,
        qe as questionnaire,
        ans as answer,
        anso as answerOption

         from EvidenceJpaEntity evd
         join UserJpaEntity u on evd.createdBy = u.id
         join AssessmentJpaEntity a on evd.assessmentId = a.id
         join AssessmentResultJpaEntity ar on a.id = ar.assessment.id
         join QuestionJpaEntity q on evd.questionId = q.id and q.kitVersionId = ar.kitVersionId
         join QuestionnaireJpaEntity qe on q.questionnaireId = qe.id and q.kitVersionId = qe.kitVersionId
         join AnswerJpaEntity ans on ans.questionId = q.id and ans.assessmentResult.id = ar.id
         join AnswerOptionJpaEntity anso on anso.id = ans.answerOptionId
         where evd.id = :id AND evd.deleted = false
        """)
    Optional<EvidenceWithDetailsView> findEvidenceWithDetailsById(@Param("id") UUID id);
}
