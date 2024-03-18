package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fau_expert_group_user_access")
@IdClass(ExpertGroupAccessId.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpertGroupAccessJpaEntity {

    @Id
    @Column(name = "expert_group_id")
    private Long expertGroupId;

    @Id
    @Column(name = "user_id")
    private UUID userId;

    @Column(name = "invite_email", columnDefinition = "TEXT")
    private String inviteEmail;

    @Column(name = "invite_expiration_date")
    private LocalDateTime inviteExpirationDate;

    @Column(name = "created_by_id")
    private UUID createdBy;

    @Column(name =  "last_modified_by")
    private UUID lastModifiedBy;

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @Column(name =  "last_modification_time")
    private LocalDateTime lastModificationTime;
}
