package org.flickit.assessment.data.jpa.core.assessmentreport;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fac_assessment_report")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentReportJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "assessment_result_id", updatable = false, nullable = false)
    private UUID assessmentResultId;

    @Column(name = "metadata", nullable = false)
    private String metadata;

    @Column(name = "published", nullable = false)
    private Boolean published;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;
}
