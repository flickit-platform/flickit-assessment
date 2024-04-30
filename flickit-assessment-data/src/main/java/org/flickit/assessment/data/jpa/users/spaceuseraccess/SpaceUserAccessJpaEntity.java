package org.flickit.assessment.data.jpa.users.spaceuseraccess;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@IdClass(SpaceUserAccessJpaEntity.EntityId.class)
@Table(name = "fau_space_user_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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


    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long spaceId;
        private UUID userId;
    }

    //TODO: Convert 'creationTime' to 'lastSeen'
    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String CREATION_TIME = "creationTime";
    }

}
