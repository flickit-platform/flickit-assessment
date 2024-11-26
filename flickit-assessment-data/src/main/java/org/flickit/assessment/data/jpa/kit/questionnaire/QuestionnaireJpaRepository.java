package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, QuestionnaireJpaEntity.EntityId> {

    List<QuestionnaireJpaEntity> findAllByKitVersionIdOrderByIndex(Long kitVersionId);

    List<QuestionnaireJpaEntity> findAllByKitVersionId(long kitVersionId);

    Optional<QuestionnaireJpaEntity> findByIdAndKitVersionId(Long id, Long kitVersionId);

    boolean existsByIdAndKitVersionId(long id, long kitVersionId);

    void deleteByIdAndKitVersionId(long id, long kitVersionId);

    List<QuestionnaireJpaEntity> findAllByIdInAndKitVersionId(Collection<Long> ids, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE QuestionnaireJpaEntity q
            SET q.title = :title,
                q.code = :code,
                q.index = :index,
                q.description = :description,
                q.lastModificationTime = :lastModificationTime,
                q.lastModifiedBy = :lastModifiedBy
            WHERE q.id = :id AND q.kitVersionId = :kitVersionId
        """)
    void update(@Param(value = "id") long id,
                @Param(value = "kitVersionId") long kitVersionId,
                @Param(value = "title") String title,
                @Param(value = "code") String code,
                @Param(value = "index") int index,
                @Param(value = "description") String description,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime,
                @Param(value = "lastModifiedBy") UUID lastModifiedBy
    );

    @Query("""
            SELECT
                q as questionnaire,
                COUNT(DISTINCT question.id) as questionCount
            FROM QuestionnaireJpaEntity q
            LEFT JOIN QuestionJpaEntity question ON q.id = question.questionnaireId AND q.kitVersionId = question.kitVersionId
            WHERE q.kitVersionId = :kitVersionId
            GROUP BY q.id, q.kitVersionId, q.index
            ORDER BY q.index
        """)
    Page<QuestionnaireListItemView> findAllWithQuestionCountByKitVersionId(@Param(value = "kitVersionId") long kitVersionId,
                                                                           Pageable pageable);
}
