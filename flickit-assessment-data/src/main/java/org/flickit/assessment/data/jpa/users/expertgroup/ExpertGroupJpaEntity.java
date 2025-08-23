package org.flickit.assessment.data.jpa.users.expertgroup;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "fau_expert_group")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class ExpertGroupJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fau_expert_group_id_seq")
    @SequenceGenerator(name = "fau_expert_group_id_seq", sequenceName = "fau_expert_group_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 100, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "bio", length = 200, nullable = false)
    private String bio;

    @Column(name = "about", nullable = false, columnDefinition = "TEXT")
    private String about;

    @Column(name = "picture", length = 100)
    private String picture;

    @Column(name = "website", length = 200)
    private String website;

    @Column(name = "owner_id", nullable = false)
    private UUID ownerId;

    @Column(name = "created_by", nullable = false)
    private UUID createdBy;

    @Column(name = "last_modified_by", nullable = false)
    private UUID lastModifiedBy;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_time", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "deleted", nullable = false)
    private boolean deleted;

    @Column(name = "deletion_time", nullable = false)
    private long deletionTime;
}
