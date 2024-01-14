package org.flickit.assessment.data.jpa.kit.assessmentkitdsl;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "baseinfo_assessmentkitdsl")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentKitDslJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_profiledsl_id_seq")
    @SequenceGenerator(name = "baseinfo_profiledsl_id_seq", sequenceName = "baseinfo_profiledsl_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "dsl_file", length = 200, nullable = false)
    private String dslPath;

    @Column(name = "json_path", length = 200)
    private String jsonPath;

    @Column(name = "assessment_kit_id")
    private Long assessmentKitId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

}
