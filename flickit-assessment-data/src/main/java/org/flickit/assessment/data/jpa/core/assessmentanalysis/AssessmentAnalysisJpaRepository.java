package org.flickit.assessment.data.jpa.core.assessmentanalysis;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentAnalysisJpaRepository extends JpaRepository<AssessmentAnalysisJpaEntity, UUID> {

}
