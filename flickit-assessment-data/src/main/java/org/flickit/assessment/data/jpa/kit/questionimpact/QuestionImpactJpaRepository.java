package org.flickit.assessment.data.jpa.kit.questionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface QuestionImpactJpaRepository extends JpaRepository<QuestionImpactJpaEntity, Long> {

    List<QuestionImpactJpaEntity> findAllByQuestionId(Long questionId);

    @Modifying
    @Query("UPDATE QuestionImpactJpaEntity q SET " +
        "q.weight = :weight " +
        "WHERE q.id = :id AND q.questionId = :questionId")
    void update(@Param("id") Long id, @Param("weight") int weight, @Param("questionId") Long questionId);

}
