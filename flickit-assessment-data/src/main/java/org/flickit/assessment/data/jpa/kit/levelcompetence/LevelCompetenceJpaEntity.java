package org.flickit.assessment.data.jpa.kit.levelcompetence;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(LevelCompetenceJpaEntity.EntityId.class)
@Table(name = "fak_level_competence")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class LevelCompetenceJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_version_id", nullable = false)
    private Long kitVersionId;

    @Column(name = "affected_level_id", nullable = false)
    private Long affectedLevelId;

    @Column(name = "effective_level_id", nullable = false)
    private Long effectiveLevelId;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

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
