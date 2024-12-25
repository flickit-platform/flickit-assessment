package org.flickit.assessment.data.jpa.advice.adviceitem;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface AdviceItemJpaRepository extends JpaRepository<AdviceItemJpaEntity, UUID> {

    Page<AdviceItemJpaEntity> findByAssessmentResultId(UUID assessmentResultId, PageRequest pageRequest);

    int countByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE AdviceItemJpaEntity a
            SET a.title = :title,
                a.description = :description,
                a.cost = :cost,
                a.priority = :priority,
                a.impact = :impact,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy = :lastModifiedBy
            WHERE a.id = :id
        """)
    void update(UUID id,
                @Param("title") String title,
                @Param("description") String description,
                @Param("cost") int cost,
                @Param("priority") int priority,
                @Param("impact") int impact,
                @Param("lastModificationTime") LocalDateTime lastModificationTime,
                @Param("lastModifiedBy") UUID lastModifiedBy);
}
