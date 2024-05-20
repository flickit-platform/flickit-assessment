package org.flickit.assessment.data.jpa.users.spaceinvitee;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fau_space_invitee")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
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

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String CREATION_TIME = "creationTime";
    }
}
