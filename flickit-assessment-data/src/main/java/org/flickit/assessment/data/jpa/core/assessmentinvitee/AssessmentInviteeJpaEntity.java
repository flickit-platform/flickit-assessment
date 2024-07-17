package org.flickit.assessment.data.jpa.core.assessmentinvitee;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fac_assessment_invitee")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentInviteeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "assessment_id", updatable = false, nullable = false)
    private UUID assessmentId;

    @Column(name = "email", nullable = false)
    private String email;

    @Column(name = "role_id", nullable = false)
    private Integer roleId;

    @Column(name = "expiration_time", nullable = false)
    private LocalDateTime expirationTime;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String CREATION_TIME = "creationTime";
    }
}
