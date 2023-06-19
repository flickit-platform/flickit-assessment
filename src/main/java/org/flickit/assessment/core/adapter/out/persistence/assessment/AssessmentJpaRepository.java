package org.flickit.assessment.core.adapter.out.persistence.assessment;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentJpaEntity, UUID> {

}
