package org.flickit.assessment.data.jpa.levelcompetence;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.maturitylevel.MaturityLevelJpaEntity;

@Entity
@Table(name = "baseinfo_levelcompetence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaturityLevelCompetenceJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "maturity_level_id", referencedColumnName = "id", nullable = false)
    private MaturityLevelJpaEntity maturityLevel;

    @ManyToOne
    @JoinColumn(name = "maturity_level_competence_id", referencedColumnName = "id")
    private MaturityLevelCompetenceJpaEntity maturityLevelCompetence;

    @Column(name = "value")
    private Integer value;
}
