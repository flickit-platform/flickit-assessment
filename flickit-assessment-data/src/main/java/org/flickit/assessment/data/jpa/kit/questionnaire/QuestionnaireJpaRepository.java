package org.flickit.assessment.data.jpa.kit.questionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface QuestionnaireJpaRepository extends JpaRepository<QuestionnaireJpaEntity, Long> {

    List<QuestionnaireJpaEntity> findAllByAssessmentKitId(Long assessmentKitId);

}
