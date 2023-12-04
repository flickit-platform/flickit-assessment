package org.flickit.assessment.data.jpa.kit.maturitylevel;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;

import java.util.List;

@Entity
@Table(name = "baseinfo_maturitylevel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaturityLevelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_maturitylevel_id_seq")
    @SequenceGenerator(name = "baseinfo_maturitylevel_id_seq", sequenceName = "baseinfo_maturitylevel_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "assessment_kit_id")
    private Long assessmentKitId;

    public MaturityLevelJpaEntity(Long id, String code, String title, Integer value, Integer index, Long assessmentKitId) {
        this.id = id;
        this.code = code;
        this.title = title;
        this.value = value;
        this.index = index;
        this.assessmentKitId = assessmentKitId;
    }

    @OneToMany(mappedBy = "affectedLevel", cascade = CascadeType.REMOVE)
    private List<LevelCompetenceJpaEntity> affectedCompetences;

    @OneToMany(mappedBy = "effectiveLevel", cascade = CascadeType.REMOVE)
    private List<LevelCompetenceJpaEntity> effectiveCompetences;

    @OneToMany(mappedBy = "maturityLevel", cascade = CascadeType.REMOVE)
    private List<QuestionImpactJpaEntity> questionImpacts;
}
