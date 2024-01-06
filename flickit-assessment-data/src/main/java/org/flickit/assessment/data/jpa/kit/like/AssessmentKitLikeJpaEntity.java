package org.flickit.assessment.data.jpa.kit.like;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.assessmentkit.AssessmentKitJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

@Entity
@Table(name = "baseinfo_assessmentkitlike")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentKitLikeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "assessment_kit_id", nullable = false)
    private AssessmentKitJpaEntity kit;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserJpaEntity user;
}
