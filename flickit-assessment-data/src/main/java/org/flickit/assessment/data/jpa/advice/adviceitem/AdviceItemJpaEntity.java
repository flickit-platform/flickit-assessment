package org.flickit.assessment.data.jpa.advice.adviceitem;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "faa_advice_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdviceItemJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @Column(name = "id", updatable = false, nullable = false)
    UUID id;

    @Column(name = "assessment_result_id", nullable = false)
    UUID assessmentResultId;

    @Column(name = "title",length = 100, nullable = false)
    String title;

    @Column(name = "description", length = 500)
    String description;

    @Column(name = "cost", nullable = false)
    int cost;

    @Column(name = "priority", nullable = false)
    int priority;

    @Column(name = "impact", nullable = false)
    int impact;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;
}
