package org.flickit.assessment.data.jpa.question;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionJpaRepository extends JpaRepository<QuestionJpaEntity, Long> {

    List<QuestionJpaEntity> loadByQuestionnaireId(Long questionnaireId);
}
