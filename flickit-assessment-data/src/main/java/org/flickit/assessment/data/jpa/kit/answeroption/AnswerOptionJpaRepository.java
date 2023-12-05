package org.flickit.assessment.data.jpa.kit.answeroption;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerOptionJpaRepository extends JpaRepository<AnswerOptionJpaEntity, Long> {

    @Modifying
    @Query("UPDATE AnswerOptionJpaEntity a SET " +
        "a.title = :title " +
        "WHERE a.id = :id")
    void update(Long id, String title);

    List<AnswerOptionJpaEntity> findByQuestionId(Long questionId);

}
