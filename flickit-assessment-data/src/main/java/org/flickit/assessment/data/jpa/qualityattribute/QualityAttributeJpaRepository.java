package org.flickit.assessment.data.jpa.qualityattribute;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QualityAttributeJpaRepository extends JpaRepository<QualityAttributeJpaEntity, Long> {

    List<QualityAttributeJpaEntity> loadByAssessmentSubjectId(Long assessmentSubjectId);
}
