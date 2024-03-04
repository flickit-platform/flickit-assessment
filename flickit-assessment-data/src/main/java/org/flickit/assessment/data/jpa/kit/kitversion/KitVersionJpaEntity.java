package org.flickit.assessment.data.jpa.kit.kitversion;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;

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

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "kit_id")
    private AssessmentKitJpaEntity kit;

    @Column(name = "status", nullable = false)
    private int status;


}
