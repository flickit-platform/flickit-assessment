package org.flickit.flickitassessmentcore.adapter.out.persistence.evidence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.UUID;

public interface EvidenceJpaRepository extends JpaRepository<EvidenceJpaEntity, UUID> {

    Page<EvidenceJpaEntity> findByQuestionIdAndAssessmentIdOrderByLastModificationTimeDesc(Long questionId, UUID assessmentId, Pageable pageable);

    @Modifying
    @Query("UPDATE EvidenceJpaEntity e SET " +
        "e.description = :description, " +
        "e.lastModificationTime = :lastModificationTime " +
        "WHERE e.id = :id")
    void update(@Param(value = "id") UUID id,
                @Param(value = "description") String description,
                @Param(value = "lastModificationTime") LocalDateTime lastModificationTime);
}
