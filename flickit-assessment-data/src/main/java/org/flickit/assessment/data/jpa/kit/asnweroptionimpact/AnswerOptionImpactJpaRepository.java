package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnswerOptionImpactJpaRepository extends JpaRepository<AnswerOptionImpactJpaEntity, AnswerOptionImpactJpaEntity.EntityId> {

    List<AnswerOptionImpactJpaEntity> findAllByQuestionImpactIdAndKitVersionId(long impactId, long kitVersionId);

    @Modifying
    @Query("""
            UPDATE AnswerOptionImpactJpaEntity a
            SET a.value = :value,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id AND a.kitVersionId = :kitVersionId
        """)
        void update(@Param("id") Long id,
                    @Param("kitVersionId") Long kitVersionId,
                    @Param("value") Double value,
                    @Param("lastModificationTime") LocalDateTime lastModificationTime,
                    @Param("lastModifiedBy") UUID lastModifiedBy);
}
