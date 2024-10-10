package org.flickit.assessment.data.jpa.kit.questionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuestionImpactJpaRepository extends JpaRepository<QuestionImpactJpaEntity, Long> {

    List<QuestionImpactJpaEntity> findAllByQuestionIdAndKitVersionId(long questionId, long kitVersionId);

    void deleteByIdAndKitVersionId(long questionImpactId, long kitVersionId);

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
}
