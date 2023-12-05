package org.flickit.assessment.data.jpa.kit.attribute;


import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "baseinfo_qualityattribute", uniqueConstraints = {
    @UniqueConstraint(name = "baseinfo_qualityattribut_title_assessment_subject_4a82494c_uniq", columnNames = {"title", "subjectId"}),
    @UniqueConstraint(name = "baseinfo_qualityattribute_code_fc2b6cd0_uniq", columnNames = {"subjectId", "code"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "description", nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "assessment_subject_id", nullable = false)
    private long subjectId;

    @Column(name = "assessment_kit_id", nullable = false)
    private long assessmentKitId;
}
