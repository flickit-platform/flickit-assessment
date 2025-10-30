package org.flickit.assessment.data.jpa.core.answerhistory;

import org.flickit.assessment.data.jpa.core.assessmentresult.AssessmentResultJpaEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface AnswerHistoryJpaRepository extends JpaRepository<AnswerHistoryJpaEntity, UUID> {

    Page<AnswerHistoryJpaEntity> findAllByAssessmentResultAndQuestionId(AssessmentResultJpaEntity assessmentResult,
                                                                        long questionId,
                                                                        Pageable pageable);

    @Query("""
            SELECT a.questionId AS questionId,
                    COUNT(a) as answerHistoryCount
            FROM AnswerHistoryJpaEntity a
            WHERE a.assessmentResult.id=:assessmentResultId AND a.questionId IN :questionIds
            GROUP BY a.questionId
        """)
    List<QuestionIdAndAnswerCountView> countByAssessmentResultIdAndQuestionIdIn(@Param("assessmentResultId") UUID assessmentResultId,
                                                                                @Param("questionIds") List<Long> questionIds);
}
