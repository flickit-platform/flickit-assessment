package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "assessment_color")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentColorJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "title", length = 40, unique = true, nullable = false)
    private String title;

    @Column(name = "color_code", length = 20, unique = true, nullable = false)
    private String colorCode;

    /*
    * TODO:
    *  - getAssessments
    * */
}
