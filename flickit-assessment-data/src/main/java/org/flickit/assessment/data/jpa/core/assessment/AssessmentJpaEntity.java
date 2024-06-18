package org.flickit.assessment.data.jpa.core.assessment;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fac_assessment")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "assessment_kit_id", nullable = false)
    private Long assessmentKitId;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "deletion_time", nullable = false)
    private Long deletionTime;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String ASSESSMENT_KIT_ID = "assessmentKitId";
        public static final String SPACE_ID = "spaceId";
        public static final String LAST_MODIFICATION_TIME = "lastModificationTime";
    }
}
