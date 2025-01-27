package org.flickit.assessment.data.jpa.core.assessmentreport;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface AssessmentReportJpaRepository extends JpaRepository<AssessmentReportJpaEntity, UUID> {

    Optional<AssessmentReportJpaEntity> findByAssessmentResultId(UUID assessmentResultId);

    @Modifying
    @Query("""
            UPDATE AssessmentReportJpaEntity a
            SET a.metadata = :metadata,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy= :lastModifiedBy
            WHERE a.id = :id
        """)
    void updateMetadata(@Param("id") UUID id,
                        @Param("metadata") String metadata,
                        @Param("lastModificationTime") LocalDateTime lastModificationTime,
                        @Param("lastModifiedBy") UUID lastModifiedBy);

    @Modifying
    @Query("""
            UPDATE AssessmentReportJpaEntity a
            SET a.published = :published,
                a.lastModificationTime = :lastModificationTime,
                a.lastModifiedBy= :lastModifiedBy
            WHERE a.id = :id
        """)
    void updatePublished(@Param("id") UUID id,
                         @Param("published") boolean published,
                         @Param("lastModificationTime") LocalDateTime lastModificationTime,
                         @Param("lastModifiedBy") UUID lastModifiedBy);
}
