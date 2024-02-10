package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> findAllByKitId(Long kitId);

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
            SELECT MAX(q.referenceNumber)
            FROM QuestionnaireJpaEntity q
            WHERE q.kitId = :kitId
        """)
    Long findLastReferenceNumberByKitId(@Param(value = "kitId") long kitId);
}
