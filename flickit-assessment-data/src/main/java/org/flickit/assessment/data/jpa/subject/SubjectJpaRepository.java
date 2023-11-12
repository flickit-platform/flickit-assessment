package org.flickit.assessment.data.jpa.subject;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectJpaRepository extends JpaRepository<SubjectJpaEntity, Long> {

    List<SubjectJpaEntity> findByAssessmentKitId(Long assessmentKitId);
}
