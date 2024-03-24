package org.flickit.assessment.data.jpa.kit.kitversion;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fak_kit_version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitVersionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_kit_version_id_seq")
    @SequenceGenerator(name = "fak_kit_version_id_seq", sequenceName = "fak_kit_version_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "kit_id", referencedColumnName = "id")
    private AssessmentKitJpaEntity kit;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;
}
