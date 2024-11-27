package org.flickit.assessment.data.jpa.advice.adviceitem;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdviceItemJpaRepository extends JpaRepository<AdviceItemJpaEntity, UUID> {
}
