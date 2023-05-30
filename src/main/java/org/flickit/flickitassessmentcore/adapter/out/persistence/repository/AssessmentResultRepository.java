package org.flickit.flickitassessmentcore.adapter.out.persistence.repository;

import org.flickit.flickitassessmentcore.domain.AssessmentResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentResultRepository extends JpaRepository<AssessmentResult, UUID> {
}
