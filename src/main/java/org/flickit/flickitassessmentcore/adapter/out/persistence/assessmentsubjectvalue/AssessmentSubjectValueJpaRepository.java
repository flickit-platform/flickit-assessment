package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentsubjectvalue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentSubjectValueJpaRepository extends JpaRepository<AssessmentSubjectValueJpaEntity, UUID> {

}
