package org.flickit.flickitassessmentcore.adapter.out.persistence.assessmentcolor;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "assessment_color")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentColorJpaEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;
    @Column(name = "title", length = 40, unique = true, nullable = false)
    private String title;
    @Column(name = "color_code", length = 20, unique = true, nullable = false)
    private String colorCode;

    @Override
    public String toString() {
        return title;
    }

}
