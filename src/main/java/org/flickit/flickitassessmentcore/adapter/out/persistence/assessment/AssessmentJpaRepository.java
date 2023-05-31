package org.flickit.flickitassessmentcore.adapter.out.persistence.assessment;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentJpaRepository extends JpaRepository<AssessmentEntity, UUID> {
}
