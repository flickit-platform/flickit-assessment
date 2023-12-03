package org.flickit.assessment.data.jpa.kit.question;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, Long> {

    @Query("SELECT q FROM QuestionJpaEntity q " +
        "WHERE q.questionnaireId IN (SELECT i FROM QuestionnaireJpaEntity i WHERE i.assessmentKitId = :kitId)")
    List<QuestionJpaEntity> findByKitId(Long kitId);

    @Modifying
    @Query("UPDATE QuestionJpaEntity q SET " +
        "q.title = :title, " +
        "q.description = :description, " +
        "q.index = :index, " +
        "q.notApplicable = :notApplicable " +
        "WHERE q.id = :id")
    void update(@Param("id") Long id,
                @Param("title") String title,
                @Param("index") Integer index,
                @Param("description") String description,
                @Param("notApplicable") Boolean notApplicable);

}
