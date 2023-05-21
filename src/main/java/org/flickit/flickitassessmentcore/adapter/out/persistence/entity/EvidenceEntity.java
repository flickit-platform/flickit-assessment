package org.flickit.flickitassessmentcore.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessment_evidence")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class EvidenceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false, columnDefinition = "uuid")
    private UUID id;
    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;
    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationDate;
    @Column(name = "created_by_id", nullable = false)
    private Long createdById;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "evidence_relation_id", nullable = false)
    private EvidenceRelationEntity evidenceRelation;

    @Override
    public String toString() {
        return id.toString();
    }
}
