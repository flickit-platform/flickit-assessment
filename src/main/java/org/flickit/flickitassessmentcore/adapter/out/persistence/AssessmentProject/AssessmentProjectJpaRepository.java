package org.flickit.flickitassessmentcore.adapter.out.persistence.AssessmentProject;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentProjectEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AssessmentProjectJpaRepository extends JpaRepository<AssessmentProjectEntity, UUID> {
}
