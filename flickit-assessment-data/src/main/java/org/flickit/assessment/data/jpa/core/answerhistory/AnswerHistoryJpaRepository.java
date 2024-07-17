package org.flickit.assessment.data.jpa.core.answerhistory;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerHistoryJpaRepository extends JpaRepository<AnswerHistoryJpaEntity, UUID> {
}
