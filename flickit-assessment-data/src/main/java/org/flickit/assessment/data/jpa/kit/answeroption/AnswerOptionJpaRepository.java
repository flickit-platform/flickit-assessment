package org.flickit.assessment.data.jpa.kit.answeroption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AnswerOptionJpaRepository extends JpaRepository<AnswerOptionJpaEntity, AnswerOptionJpaEntity.EntityId> {

    List<AnswerOptionJpaEntity> findByQuestionIdAndKitVersionId(Long questionId, Long kitVersionId);

    Optional<AnswerOptionJpaEntity> findByIdAndKitVersionId(Long id, Long kitVersionId);

    List<AnswerOptionJpaEntity> findAllByIdInAndKitVersionId(List<Long> allAnswerOptionIds, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE AnswerOptionJpaEntity a
            SET a.title = :title,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id AND a.kitVersionId = :kitVersionId
        """)
    void update(@Param("id") Long id,
                @Param("kitVersionId") Long kitVersionId,
                @Param("title") String title,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);

    @Query("""
            SELECT a
            FROM AnswerOptionJpaEntity a
            JOIN QuestionJpaEntity q ON a.questionId = q.id AND a.kitVersionId = q.kitVersionId
            WHERE a.questionId IN :questionIds AND a.kitVersionId = :kitVersionId
            ORDER BY q.index, a.index
        """)
    List<AnswerOptionJpaEntity> findAllByQuestionIdInAndKitVersionIdOrderByQuestionIdIndex(@Param("questionIds") List<Long> questionIds,
                                                                                           @Param("kitVersionId") Long kitVersionId);
}
