package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AnswerOptionImpactJpaRepository extends JpaRepository<AnswerOptionImpactJpaEntity, Long> {

    List<AnswerOptionImpactJpaEntity> findAllByQuestionImpactId(Long impactId);

    void deleteByQuestionImpactIdAndOptionId(Long questionImpactId, Long optionId);

    @Modifying
    @Query("UPDATE AnswerOptionImpactJpaEntity a SET " +
        "a.value = :value " +
        "WHERE a.id = :id")
    void update(@Param("id") Long id, @Param("value") Double value);
}
