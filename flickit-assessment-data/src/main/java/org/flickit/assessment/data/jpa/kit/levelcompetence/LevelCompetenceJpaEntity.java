package org.flickit.assessment.data.jpa.kit.levelcompetence;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fak_level_competence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LevelCompetenceJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_level_competence_id_seq")
    @SequenceGenerator(name = "fak_level_competence_id_seq", sequenceName = "fak_level_competence_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "affected_level_id", nullable = false)
    private Long affectedLevelId;

    @Column(name = "effective_level_id", nullable = false)
    private Long effectiveLevelId;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;
}
