package org.flickit.assessment.data.jpa.users.spaceinvitee;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fau_space_invitee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SpaceInviteeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false)
    UUID id;

    @Column(name = "space_id", nullable = false)
    long spaceId;

    @Column(name = "email", nullable = false)
    String email;

    @Column(name = "created_by", nullable = false)
    UUID createdBy;

    @Column(name = "creation_time", nullable = false)
    LocalDateTime creationTime;

    @Column(name = "expiration_date", nullable = false)
    LocalDateTime expirationDate;
}
