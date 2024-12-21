package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.AbstractEntity;

import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(AssessmentUserRoleJpaEntity.EntityId.class)
@Table(name = "fac_assessment_user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class AssessmentUserRoleJpaEntity extends AbstractEntity<AssessmentUserRoleJpaEntity.EntityId> {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "assessment_id", nullable = false)
    private UUID assessmentId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Override
    public EntityId getId() {
        return new EntityId(assessmentId, userId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private UUID assessmentId;
        private UUID userId;
    }
}
