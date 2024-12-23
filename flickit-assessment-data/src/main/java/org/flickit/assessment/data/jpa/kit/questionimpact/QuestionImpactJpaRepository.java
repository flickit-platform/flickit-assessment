package org.flickit.assessment.data.jpa.kit.questionimpact;

import org.flickit.assessment.data.jpa.kit.questionnaire.QuestionnaireJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionImpactJpaRepository extends JpaRepository<QuestionImpactJpaEntity, QuestionnaireJpaEntity.EntityId> {

    List<QuestionImpactJpaEntity> findAllByQuestionIdAndKitVersionId(long questionId, long kitVersionId);

    boolean existsByIdAndKitVersionId(long questionImpactId, long kitVersionId);

    void deleteByIdAndKitVersionId(long questionImpactId, long kitVersionId);

    Optional<QuestionImpactJpaEntity> findByIdAndKitVersionId(long id, long kitVersionId);

    List<QuestionImpactJpaEntity> findAllByKitVersionId(long kitVersionId);

    @Modifying
    @Query("""
            UPDATE QuestionImpactJpaEntity q
            SET q.weight = :weight,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id AND q.kitVersionId = :kitVersionId AND q.questionId = :questionId
        """)
    void updateWeight(@Param("id") Long id,
                @Param("kitVersionId") Long kitVersionId,
                @Param("weight") int weight,
                @Param("questionId") Long questionId,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE QuestionImpactJpaEntity q
            SET q.weight = :weight,
                q.attributeId = :attributeId,
                q.maturityLevelId = :maturityLevelId,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id AND q.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") long id,
                @Param("kitVersionId") long kitVersionId,
                @Param("weight") int weight,
                @Param("attributeId") long attributeId,
                @Param("maturityLevelId") long maturityLevelId,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);
}
