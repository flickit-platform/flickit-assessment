package org.flickit.assessment.data.jpa.kit.attribute;


import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(AttributeJpaEntity.EntityId.class)
@Table(name = "fak_attribute")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "weight", nullable = false)
    private Integer weight;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "subject_id", nullable = false)
    private Long subjectId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId {

        private Long id;
        private Long kitVersionId;
    }

    public void prepareForClone(long updatingKitVersionId, UUID clonedBy, LocalDateTime cloneTime) {
        setKitVersionId(updatingKitVersionId);
        setCreationTime(cloneTime);
        setLastModificationTime(cloneTime);
        setCreatedBy(clonedBy);
        setLastModifiedBy(clonedBy);
    }
}
