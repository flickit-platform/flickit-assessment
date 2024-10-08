package org.flickit.assessment.data.jpa.kit.questionimpact;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuestionImpactJpaRepository extends JpaRepository<QuestionImpactJpaEntity, Long> {

    List<QuestionImpactJpaEntity> findAllByQuestionIdAndKitVersionId(long questionId, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE QuestionImpactJpaEntity q SET
                q.weight = :weight,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id AND q.questionId = :questionId
        """)
    void update(@Param("id") Long id,
                @Param("weight") int weight,
                @Param("questionId") Long questionId,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Override
    @Modifying
    @Query("DELETE FROM QuestionImpactJpaEntity qi where qi.id = :id")
    void deleteById(@NotNull Long id);

    @Modifying
    @Query("""
            UPDATE QuestionImpactJpaEntity q SET
                q.weight = :weight,
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
