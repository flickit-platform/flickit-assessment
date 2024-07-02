package org.flickit.assessment.data.jpa.users.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fau_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class UserJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "email", length = 254, nullable = false)
    private String email;

    @Column(name = "display_name", nullable = false)
    private String displayName;

    @Column(name = "bio", length = 400)
    private String bio;

    @Column(name = "linkedin", length = 200)
    private String linkedin;

    @Column(name = "picture", length = 100)
    private String picture;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    @Column(name = "is_superuser", nullable = false)
    private Boolean isSuperUser;

    @Column(name = "is_staff", nullable = false)
    private Boolean isStaff;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "password", length = 128, nullable = false)
    private String password;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String NAME = "displayName";
    }

    public UserJpaEntity(UUID id, String email, String displayName, Boolean isSuperUser, Boolean isStaff,
                         Boolean isActive, String password) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.isSuperUser = isSuperUser;
        this.isStaff = isStaff;
        this.isActive = isActive;
        this.password = password;
    }
}
