package org.flickit.assessment.data.jpa.core.evidenceattachment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

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
    @EqualsAndHashCode.Include
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", nullable = false, updatable = false, columnDefinition = "uuid")
    private UUID id;

    @Column(name = "evidence_id", nullable = false, columnDefinition = "uuid")
    private UUID evidenceId;

    @Column(name = "file_path", nullable = false)
    private String filePath;

    @Column(name = "description", length = 100)
    private String description;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;
}
