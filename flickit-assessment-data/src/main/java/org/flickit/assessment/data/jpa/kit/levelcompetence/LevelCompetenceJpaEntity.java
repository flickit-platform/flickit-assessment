package org.flickit.assessment.data.jpa.kit.levelcompetence;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.maturitylevel.MaturityLevelJpaEntity;

@Entity
@Table(name = "baseinfo_levelcompetence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LevelCompetenceJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_levelcompetence_id_seq")
    @SequenceGenerator(name = "baseinfo_levelcompetence_id_seq", sequenceName = "baseinfo_levelcompetence_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maturity_level_id", referencedColumnName = "id", nullable = false)
    private MaturityLevelJpaEntity affectedLevel;

    @ManyToOne
    @JoinColumn(name = "maturity_level_competence_id", referencedColumnName = "id")
    private MaturityLevelJpaEntity effectiveLevel;

    @Column(name = "value")
    private Integer value;
}
