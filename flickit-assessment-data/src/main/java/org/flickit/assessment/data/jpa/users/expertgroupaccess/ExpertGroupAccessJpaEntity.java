package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "baseinfo_expertgroupaccess")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpertGroupAccessJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_expertgroupaccess_id_seq")
    @SequenceGenerator(name = "baseinfo_expertgroupaccess_id_seq", sequenceName = "baseinfo_expertgroupaccess_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "expert_group_id", nullable = false)
    private Long expertGroupId;

    @Column(name = "invite_date")
    private LocalDateTime inviteDate;

    @Column(name = "invite_expiration_date")
    private LocalDateTime inviteExpirationDate;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "invite_token")
    private UUID inviteToken;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private int status;
}
