package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AnswerOptionImpactJpaRepository extends JpaRepository<AnswerOptionImpactJpaEntity, Long> {

    List<AnswerOptionImpactJpaEntity> findAllByQuestionImpact(Long impactId);
}
