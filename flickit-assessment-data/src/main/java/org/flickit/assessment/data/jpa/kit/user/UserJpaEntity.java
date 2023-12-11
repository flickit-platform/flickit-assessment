package org.flickit.assessment.data.jpa.kit.user;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

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
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "account_user_id_seq")
    @SequenceGenerator(name = "account_user_id_seq", sequenceName = "account_user_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_superuser", nullable = false)
    private Boolean isSuperuser;

    @Column(name = "is_staff", nullable = false)
    private Boolean isStaff;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Column(name = "current_space_id")
    private Long currentSpaceId;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "bio", length = 400)
    private String bio;

    @Column(name = "linkedin", length = 200)
    private String linkedin;

    @Column(name = "picture", length = 100)
    private String picture;

    @Column(name = "default_space_id")
    private Long defaultSpaceId;

}
