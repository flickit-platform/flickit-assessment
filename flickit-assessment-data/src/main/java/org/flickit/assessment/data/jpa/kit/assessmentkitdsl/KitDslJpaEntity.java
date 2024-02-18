package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fak_kit_dsl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitDslJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_kit_dsl_id_seq")
    @SequenceGenerator(name = "fak_kit_dsl_id_seq", sequenceName = "fak_kit_dsl_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "dsl_path", length = 200, nullable = false)
    private String dslPath;

    @Column(name = "json_path", length = 200)
    private String jsonPath;

    @Column(name = "assessment_kit_id")
    private Long kitId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

}
