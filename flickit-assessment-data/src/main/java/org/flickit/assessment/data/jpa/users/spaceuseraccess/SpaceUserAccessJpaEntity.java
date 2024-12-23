package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(SpaceUserAccessJpaEntity.EntityId.class)
@Table(name = "fau_space_user_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SpaceUserAccessJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_seen", nullable = false)
    private LocalDateTime lastSeen;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long spaceId;
        private UUID userId;
    }
}
