package org.flickit.assessment.data.jpa.kit.attribute;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "baseinfo_qualityattribute")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_qualityattribute_id_seq")
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationDate;

    @Column(name = "index")
    private Integer index;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "assessment_subject_id", nullable = false)
    private Long subjectId;

    @Column(name = "assessment_kit_id", nullable = false)
    private Long kitId;
}
