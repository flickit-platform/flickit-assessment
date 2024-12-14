package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AnswerOptionImpactJpaRepository extends JpaRepository<AnswerOptionImpactJpaEntity, AnswerOptionImpactJpaEntity.EntityId> {


    @Query("""
            SELECT oi as optionImpact,
                qi as questionImpact
            FROM AnswerOptionImpactJpaEntity oi
            JOIN QuestionImpactJpaEntity qi ON oi.questionImpactId = qi.id AND oi.kitVersionId = qi.kitVersionId
            WHERE oi.kitVersionId = :kitVersionId AND oi.optionId IN :optionIds
        """)
    List<OptionImpactWithQuestionImpactView> findAllByOptionIdInAndKitVersionId(List<Long> optionIds, long kitVersionId);
}
