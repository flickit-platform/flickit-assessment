package org.flickit.assessment.data.jpa.kit.spaceuseraccess;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(SpaceUserAccessJpaEntity.EntityId.class)
@Table(name = "fau_space")
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

    @Column(name = "invite_email")
    private String inviteEmail;

    @Column(name = "invite_expiration_date")
    private LocalDateTime inviteExpirationDate;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long spaceId;
        private UUID userId;
    }

}
