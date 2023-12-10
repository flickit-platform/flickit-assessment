package org.flickit.assessment.data.jpa.kit.subjectquestionnaire;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface SubjectQuestionnaireJpaRepository extends JpaRepository<SubjectQuestionnaireJpaEntity, Long> {

    @Query("FROM SubjectQuestionnaireJpaEntity sq " +
        "where sq.subjectId in " +
        "(SELECT s.id FROM SubjectJpaEntity s where s.assessmentKitId = :assessmentKitId)")
    List<SubjectQuestionnaireJpaEntity> findAllByAssessmentKitId(Long assessmentKitId);
}
