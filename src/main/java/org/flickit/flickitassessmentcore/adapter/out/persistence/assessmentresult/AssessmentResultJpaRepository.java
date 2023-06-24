package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentresult;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentResultJpaRepository extends JpaRepository<AssessmentResultJpaEntity, UUID> {

}
