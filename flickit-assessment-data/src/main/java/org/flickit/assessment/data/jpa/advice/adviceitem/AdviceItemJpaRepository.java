package org.flickit.assessment.data.jpa.advice.adviceitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdviceItemJpaRepository extends JpaRepository<AdviceItemJpaEntity, UUID> {

    Page<AdviceItemJpaEntity> findByAssessmentResultId(UUID assessmentResultId, PageRequest pageRequest);
}
