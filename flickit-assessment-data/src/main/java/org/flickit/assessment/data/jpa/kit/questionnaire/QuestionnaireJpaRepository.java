package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> findAllByAssessmentKitId(Long assessmentKitId);

    @Modifying
    @Query("UPDATE QuestionnaireJpaEntity q SET " +
        "q.title = :title, " +
        "q.description = :description, " +
        "q.index = :index " +
        "WHERE q.assessmentKitId = :kitId")
    void updateByKitId(@Param("title") String title,
                       @Param("description") String description,
                       @Param("index") Integer index,
                       @Param("kitId") Long kitId);
}
