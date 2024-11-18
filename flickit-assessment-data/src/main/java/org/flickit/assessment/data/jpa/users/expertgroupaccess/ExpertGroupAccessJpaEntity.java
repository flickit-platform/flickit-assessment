package org.flickit.assessment.data.jpa.users.expertgroupaccess;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@IdClass(ExpertGroupAccessJpaEntity.EntityId.class)
@Table(name = "fau_expert_group_user_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class ExpertGroupAccessJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "expert_group_id", nullable = false)
    private Long expertGroupId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "invite_date")
    private LocalDateTime inviteDate;

    @Column(name = "invite_expiration_date")
    private LocalDateTime inviteExpirationDate;

    @Column(name = "invite_token")
    private UUID inviteToken;

    @Column(name = "status", nullable = false)
    private int status;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name =  "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name =  "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name= "last_seen")
    private LocalDateTime lastSeen;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long expertGroupId;
        private UUID userId;
    }
}
