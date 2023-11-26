package org.flickit.assessment.data.jpa.kit.questionimpact;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionImpactJpaRepository extends JpaRepository<QuestionImpactJpaEntity, Long> {

    List<QuestionImpactJpaEntity> findAllByQuestionId(Long questionId);
}
