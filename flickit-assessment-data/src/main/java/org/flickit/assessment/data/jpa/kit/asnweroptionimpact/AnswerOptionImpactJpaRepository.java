package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnswerOptionImpactJpaRepository extends JpaRepository<AnswerOptionImpactJpaEntity, Long> {

    List<AnswerOptionImpactJpaEntity> findAllByQuestionImpactIdAndKitVersionId(long impactId, long kitVersionId);

    @Query("""
            SELECT oi as optionImpact,
                qi as questionImpact
            FROM AnswerOptionImpactJpaEntity oi
            JOIN QuestionImpactJpaEntity qi ON oi.questionImpactId = qi.id AND oi.kitVersionId = qi.kitVersionId
            WHERE oi.kitVersionId = :kitVersionId AND oi.optionId IN :optionIds
        """)
    List<OptionImpactWithQuestionImpactView> findAllByOptionIdInAndKitVersionId(List<Long> optionIds, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE AnswerOptionImpactJpaEntity a
            SET a.value = :value,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id
        """)
        void update(@Param("id") Long id,
                    @Param("value") Double value,
                    @Param("lastModificationTime") LocalDateTime lastModificationTime,
                    @Param("lastModifiedBy") UUID lastModifiedBy);
}
