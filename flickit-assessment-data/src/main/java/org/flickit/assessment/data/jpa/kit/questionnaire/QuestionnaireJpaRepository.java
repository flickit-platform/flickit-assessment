package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> findAllByAssessmentKitId(Long assessmentKitId);

    @Modifying
    @Query("UPDATE QuestionnaireJpaEntity q SET " +
        "q.title = :title, " +
        "q.index = :index, " +
        "q.description = :description, " +
        "q.lastModificationTime = :lastModificationTime " +
        "WHERE q.id = :id")
    void update(
        @Param(value = "id") long id,
        @Param(value = "title") String title,
        @Param(value = "index") int index,
        @Param(value = "description") String description,
        @Param(value = "lastModificationTime") LocalDateTime lastModificationTime
    );
}
