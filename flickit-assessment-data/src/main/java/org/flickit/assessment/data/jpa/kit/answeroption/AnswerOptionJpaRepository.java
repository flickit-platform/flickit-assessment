package org.flickit.assessment.data.jpa.kit.answeroption;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AnswerOptionJpaRepository extends JpaRepository<AnswerOptionJpaEntity, Long> {
    AnswerOptionJpaEntity findByIndexAndQuestionId(int index, Long questionId);
}
