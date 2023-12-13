package org.flickit.assessment.data.jpa.kit.expertgroup;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

@Entity
@Table(name = "baseinfo_expertgroup")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpertGroupJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "about", nullable = false, columnDefinition = "TEXT")
    private String about;

    @Column(name = "picture", nullable = false)
    private String picture;

    @Column(name = "website", length = 200, nullable = false)
    private String website;

    @Column(name = "bio", length = 200, nullable = false)
    private String bio;

    @ManyToOne
    @JoinColumn(name = "owner_id", referencedColumnName = "id", nullable = false)
    private UserJpaEntity owner;
}
