package org.flickit.assessment.data.jpa.kit.spaceuseraccess;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_useraccess")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class SpaceUserAccessJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_useraccess_id_seq")
    @SequenceGenerator(name = "account_useraccess_id_seq", sequenceName = "account_useraccess_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "space_id", nullable = false)
    private Long spaceId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "invite_email")
    private String inviteEmail;

    @Column(name = "invite_expiration_date")
    private LocalDateTime inviteExpirationDate;

    @Column(name= "last_seen")
    private LocalDateTime lastSeen;

}
