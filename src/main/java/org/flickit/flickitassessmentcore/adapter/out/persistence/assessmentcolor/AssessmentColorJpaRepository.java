package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import org.flickit.flickitassessmentcore.adapter.out.persistence.entity.AssessmentColorEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssessmentColorJpaRepository extends JpaRepository<AssessmentColorEntity, Long> {
    boolean existsById(Long id);
}
