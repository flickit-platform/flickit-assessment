package org.flickit.flickitassessmentcore.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "assessment_assessmentproject",
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"title", "space_id"}),
        @UniqueConstraint(columnNames = {"code", "space_id"})
    })
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class AssessmentProjectEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @Column(name = "code", length = 100, nullable = false)
    private String code;
    @Column(name = "title", length = 100, nullable = false)
    private String title;
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;
    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;
    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationDate;
    @Column(name = "assessment_kit_id", nullable = false)
    private Long assessmentKitId;
    @ManyToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "color_id", referencedColumnName = "id")
    private AssessmentColorEntity colorId;
    @Column(name = "space_id", nullable = false)
    private Long spaceId;
    @Column(name = "maturity_level_id")
    private Long maturityLevelId;

    @Override
    public String toString() {
        return title;
    }

    /* TODO:
     *  - getAssessmentResult
     */
}
