package org.flickit.assessment.data.jpa.kit.asnweroptionimpact;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface AnswerOptionImpactJpaRepository extends JpaRepository<AnswerOptionImpactJpaEntity, Long> {

    List<AnswerOptionImpactJpaEntity> findAllByQuestionImpactId(Long impactId);

    List<AnswerOptionImpactJpaEntity> findAllByOptionIdInAndKitVersionId(List<Long> optionIds, long kitVersionId);

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
