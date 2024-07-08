package org.flickit.assessment.data.jpa.core.evidenceattachment;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EvidenceAttachmentJpaRepository extends JpaRepository<EvidenceAttachmentJpaEntity, UUID> {

    int countByEvidenceId(UUID evidenceId);

    @Query("""
        SELECT e.id as id,
            e.filePath as filePath,
            e.description as description,
            u.id as userId,
            u.displayName as displayName,
            e.creationTime as creationTime
        FROM EvidenceAttachmentJpaEntity e
        join UserJpaEntity u on e.createdBy = u.id
        WHERE e.evidenceId = :evidenceId
        """)
    List<EvidenceAttachmentWithUserView> findByEvidenceId(@Param("evidenceId") UUID evidenceId);
}
