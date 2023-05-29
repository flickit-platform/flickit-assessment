package org.flickit.flickitassessmentcore.adapter.out.persistence.repository;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentRepository extends JpaRepository<AssessmentEntity, UUID> {
}
