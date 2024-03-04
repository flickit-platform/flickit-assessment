package org.flickit.assessment.data.jpa.kit.kitversion;

import jakarta.persistence.*;
import lombok.*;

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

    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Column(name = "version_status", nullable = false)
    private int versionStatus;


}
