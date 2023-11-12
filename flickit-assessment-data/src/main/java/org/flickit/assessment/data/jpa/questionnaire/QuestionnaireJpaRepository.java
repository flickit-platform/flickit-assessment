package org.flickit.assessment.data.jpa.questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> loadByAssessmentKitId(Long assessmentKitId);
}
