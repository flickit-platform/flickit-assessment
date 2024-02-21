package org.flickit.assessment.data.jpa.advice.advicequestion;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AdviceQuestionJpaRepository extends JpaRepository<AdviceQuestionJpaEntity, UUID> {
}
