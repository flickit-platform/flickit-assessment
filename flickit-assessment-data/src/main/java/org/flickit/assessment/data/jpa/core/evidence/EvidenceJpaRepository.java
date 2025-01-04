package org.flickit.assessment.data.jpa.core.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    boolean existsByIdAndDeletedFalse(UUID evidenceId);

    Optional<EvidenceJpaEntity> findByIdAndDeletedFalse(UUID id);

    @Query("""
            SELECT
                e.id as id,
                e.description as description,
                e.type as type,
                e.createdBy as createdBy,
                e.lastModificationTime as lastModificationTime,
                COUNT (a) as attachmentsCount
            FROM EvidenceJpaEntity e
            LEFT JOIN EvidenceAttachmentJpaEntity a ON e.id = a.evidenceId
            WHERE e.questionId = :questionId AND e.assessmentId = :assessmentId AND e.deleted = false
                    AND ((e.type IS NULL AND (e.resolved IS NULL OR e.resolved = false))
                        OR (e.type IS NOT NULL AND e.resolved IS NULL))
            GROUP BY e.id, e.description, e.type, e.createdBy, e.lastModificationTime
        """)
    Page<EvidenceWithAttachmentsCountView> findByQuestionIdAndAssessmentId(@Param("questionId") Long questionId,
                                                                           @Param("assessmentId") UUID assessmentId,
                                                                           Pageable pageable);

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

    @Modifying
    @Query("""
            UPDATE EvidenceJpaEntity e
            SET e.resolved = true,
                e.lastModifiedBy = :lastModifiedBy,
                e.lastModificationTime = :lastModificationTime
            WHERE e.id = :evidenceId
        """)
    void resolveComment(@Param("evidenceId") UUID evidenceId,
                        @Param("lastModifiedBy") UUID lastModifiedBy,
                        @Param("lastModificationTime") LocalDateTime lastModificationTime);

    @Query("""
            SELECT COUNT(DISTINCT e.questionId)
            FROM EvidenceJpaEntity e
            LEFT JOIN AnswerJpaEntity a ON e.questionId = a.questionId AND a.assessmentResult.assessment.id = :assessmentId
            LEFT JOIN AssessmentResultJpaEntity ar on a.assessmentResult.assessment.id = e.assessmentId
            WHERE e.assessmentId = :assessmentId
                AND (a.answerOptionId IS NOT NULL OR a.isNotApplicable = true)
                AND e.deleted = false
                AND e.type IS NOT NULL
        """)
    int countAnsweredQuestionsHavingEvidence(@Param("assessmentId") UUID assessmentId);

    @Query("""
            SELECT COUNT(e.id)
            FROM EvidenceJpaEntity e
            WHERE e.assessmentId = :assessmentId
                AND e.deleted = false
                AND e.type IS NULL
                AND (e.resolved IS NULL OR e.resolved = false)
        """)
    int countUnresolvedComments(@Param("assessmentId") UUID assessmentId);

    @Query("""
            SELECT q.questionnaireId as questionnaireId,
            COUNT(e) as count
            FROM EvidenceJpaEntity e
            JOIN QuestionJpaEntity q ON e.questionId = q.id
            WHERE e.assessmentId = :assessmentId
                 AND q.kitVersionId = :kitVersionId
                 AND q.questionnaireId IN :questionnaireIds
                 AND e.type IS NOT NULL
                 AND e.deleted = false
            GROUP BY q.questionnaireId
        """)
    List<EvidencesQuestionnaireAndCountView> countQuestionnairesQuestionsHavingEvidence(@Param("assessmentId") UUID assessmentId,
                                                                                        @Param("kitVersionId") long kitVersionId,
                                                                                        @Param("questionnaireIds") ArrayList<Long> questionnaireIds);

    @Query("""
            SELECT q.questionnaireId as questionnaireId,
            COUNT(e) as count
            FROM EvidenceJpaEntity e
            JOIN QuestionJpaEntity q ON e.questionId = q.id
            WHERE e.assessmentId = :assessmentId
                 AND q.kitVersionId = :kitVersionId
                 AND q.questionnaireId IN :questionnaireIds
                 AND e.type IS NULL
                 AND e.resolved IS NULL
                 AND e.deleted = false
            GROUP BY q.questionnaireId
        """)
    List<EvidencesQuestionnaireAndCountView> countQuestionnairesUnresolvedComments(@Param("assessmentId") UUID assessmentId,
                                                                                   @Param("kitVersionId") long kitVersionId,
                                                                                   @Param("questionnaireIds") ArrayList<Long> questionnaireIds);
}
