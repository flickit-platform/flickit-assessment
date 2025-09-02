package org.flickit.assessment.data.jpa.users.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fau_user")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
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

    @Column(name = "creation_time")
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time")
    private LocalDateTime lastModificationTime;

    public UserJpaEntity(UUID id, String email, String displayName, LocalDateTime creationTime, LocalDateTime lastModificationTime) {
        this.id = id;
        this.email = email;
        this.displayName = displayName;
        this.creationTime = creationTime;
        this.lastModificationTime = lastModificationTime;
    }
}
