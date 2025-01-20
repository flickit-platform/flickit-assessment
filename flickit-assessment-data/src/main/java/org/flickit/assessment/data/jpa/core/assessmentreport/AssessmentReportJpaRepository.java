package org.flickit.assessment.data.jpa.core.assessmentreport;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentReportJpaRepository extends JpaRepository<AssessmentReportJpaEntity, UUID> {
}
