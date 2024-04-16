package org.flickit.assessment.data.jpa.users.spaceinvitee;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fau_space_invitee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SpaceInviteeJpaEntity {

    @Column(name = "id", nullable = false)
    UUID id;

    @Column(name = "space_id", nullable = false)
    long spaceId;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "expiration_time", nullable = false)
    LocalDateTime expirationDate;

    @Column(name = "creation_time", nullable = false)
    LocalDateTime creationTime;

    @Column(name = "created_by", nullable = false)
    UUID createdBy;
}
