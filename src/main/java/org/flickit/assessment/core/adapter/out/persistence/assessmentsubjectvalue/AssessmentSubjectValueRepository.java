package org.flickit.assessment.core.adapter.out.persistence.assessmentsubjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentSubjectValueRepository extends JpaRepository<AssessmentSubjectValueJpaEntity, UUID> {

}
