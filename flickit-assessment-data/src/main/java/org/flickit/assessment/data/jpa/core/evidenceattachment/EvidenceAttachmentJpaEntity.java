package org.flickit.assessment.data.jpa.core.evidenceattachment;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_evidence_attachment")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class EvidenceAttachmentJpaEntity {

    @Id
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evidence_id", nullable = false, columnDefinition = "uuid")
    private UUID evidenceId;

    @Column(name = "false", nullable = false)
    private String file;

    @Column(name = "created_by", nullable = false)
    private String createdBy;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

}
