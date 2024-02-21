package org.flickit.assessment.data.jpa.advice.advice;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdviceJpaRepository extends JpaRepository<AdviceJpaEntity, UUID> {
}
