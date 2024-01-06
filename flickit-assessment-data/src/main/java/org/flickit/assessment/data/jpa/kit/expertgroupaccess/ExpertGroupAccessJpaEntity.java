package org.flickit.assessment.data.jpa.kit.expertgroupaccess;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

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
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "expert_group_id", nullable = false)
    private Long expertGroupId;

    @Column(name = "invite_email")
    private UUID inviteEmail;

    @Column(name = "invite_expiration_date")
    private UUID inviteExpirationDate;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
