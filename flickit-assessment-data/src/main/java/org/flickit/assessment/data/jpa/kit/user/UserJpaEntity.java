package org.flickit.assessment.data.jpa.kit.user;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "account_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email")
    private String email;

    @Column(name = "display_name")
    private String displayName;

    @Column(name = "bio")
    private String bio;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_superuser")
    private boolean isSuperUser;

    @Column(name = "is_staff")
    private boolean isStaff;

    @Column(name = "is_active")
    private boolean isActive;

    @Column(name = "current_space_id")
    private Long currentSpaceId;

    @Column(name = "default_space_id")
    private Long defaultSpaceId;
}
