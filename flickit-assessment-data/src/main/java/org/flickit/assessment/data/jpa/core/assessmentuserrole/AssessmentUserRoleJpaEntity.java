package org.flickit.assessment.data.jpa.core.assessmentuserrole;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(AssessmentUserRoleJpaEntity.EntityId.class)
@Table(name = "fac_assessment_user_role")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentUserRoleJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "assessment_id", nullable = false)
    private Long assessmentId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long assessmentId;
        private UUID userId;
    }
}
