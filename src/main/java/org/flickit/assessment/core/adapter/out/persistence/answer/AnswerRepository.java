package org.flickit.assessment.core.adapter.out.persistence.answer;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnswerRepository extends JpaRepository<AnswerJpaEntity, UUID> {

    @Query("select a from AnswerJpaEntity a " +
        "where a.assessmentResult.id = :resultId")
    List<AnswerJpaEntity> findAnswersByResultId(@Param("resultId") UUID resultId);
}
