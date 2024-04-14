package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> findAllByKitVersionId(Long kitVersionId);

    @Modifying
    @Query("""
        UPDATE QuestionnaireJpaEntity q
        SET q.title = :title,
        q.index = :index,
        q.description = :description,
        q.lastModificationTime = :lastModificationTime,
        q.lastModifiedBy = :lastModifiedBy
        WHERE q.id = :id
        """)
    void update(
        @Param(value = "id") long id,
        @Param(value = "title") String title,
        @Param(value = "index") int index,
        @Param(value = "description") String description,
        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
        @Param(value = "lastModifiedBy") UUID lastModifiedBy
    );

    @Query("""
        SELECT qn
        FROM AssessmentKitJpaEntity k
            JOIN KitVersionJpaEntity kv ON k.id = kv.kit.id
            JOIN QuestionnaireJpaEntity qn ON qn.kitVersionId = kv.id
        WHERE qn.id = :questionnaireId AND k.id = :kitId
    """)
    Optional<QuestionnaireJpaEntity> findQuestionnaireByIdAndKitId(Long questionnaireId, Long kitId);

    @Query("""
        SELECT CASE WHEN EXISTS
            (SELECT 1
             FROM QuestionnaireJpaEntity qn
                JOIN KitVersionJpaEntity kv ON qn.kitVersionId = kv.id
                JOIN AssessmentKitJpaEntity k ON kv.kit.id = k.id
             WHERE qn.id = :questionnaireId AND k.id = :kitId)
        THEN TRUE ELSE FALSE END
    """)
    boolean existsByIdAndKitId(@Param("questionnaireId")Long questionnaireId, @Param("kitId")Long kitId);
}
