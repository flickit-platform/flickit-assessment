package org.flickit.assessment.data.jpa.kit.maturitylevel;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.levelcompetence.LevelCompetenceJpaEntity;
import org.flickit.assessment.data.jpa.kit.questionimpact.QuestionImpactJpaEntity;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "fak_maturity_level")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaturityLevelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_maturity_level_id_seq")
    @SequenceGenerator(name = "fak_maturity_level_id_seq", sequenceName = "fak_maturity_level_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "ref_num", nullable = false)
    private UUID refNum;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "kit_version_id")
    private Long kitVersionId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    public MaturityLevelJpaEntity(Long id) {
        this.id = id;
    }

    @OneToMany(mappedBy = "affectedLevel", cascade = CascadeType.REMOVE)
    private List<LevelCompetenceJpaEntity> affectedCompetences;

    @OneToMany(mappedBy = "effectiveLevel", cascade = CascadeType.REMOVE)
    private List<LevelCompetenceJpaEntity> effectiveCompetences;

    @OneToMany(mappedBy = "maturityLevel", cascade = CascadeType.REMOVE)
    private List<QuestionImpactJpaEntity> questionImpacts;

    public MaturityLevelJpaEntity(Long id,
                                  UUID refNum,
                                  String code,
                                  Integer index,
                                  String title,
                                  Integer value,
                                  Long kitVersionId,
                                  LocalDateTime creationTime,
                                  LocalDateTime lastModificationTime,
                                  UUID createdBy,
                                  UUID lastModifiedBy) {
        this.id = id;
        this.refNum = refNum;
        this.code = code;
        this.index = index;
        this.title = title;
        this.value = value;
        this.kitVersionId = kitVersionId;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
    }
}


