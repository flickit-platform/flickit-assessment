package org.flickit.assessment.data.jpa.maturitylevel;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.assessmentkit.AssessmentKitJpaEntity;

@Entity
@Table(name = "baseinfo_questionnaire")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaturityLevelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "value", nullable = false)
    private Integer value;

    @ManyToOne
    @JoinColumn(name = "assessment_kit_id", referencedColumnName = "id")
    private AssessmentKitJpaEntity assessmentKit;
}
