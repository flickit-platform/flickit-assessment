package org.flickit.assessment.data.jpa.core.answerhistory;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AnswerHistoryJpaRepository extends JpaRepository<AnswerHistoryJpaEntity, UUID> {

    Page<AnswerHistoryJpaEntity> findAllByAssessmentResultAndQuestionId(AssessmentResultJpaEntity assessmentResult,
                                                                        long questionId,
                                                                        Pageable pageable);
}
