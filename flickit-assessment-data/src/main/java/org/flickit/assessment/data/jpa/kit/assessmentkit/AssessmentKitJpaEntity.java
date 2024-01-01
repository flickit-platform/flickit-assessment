package org.flickit.assessment.data.jpa.kit.assessmentkit;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.kit.like.AssessmentKitLikeJpaEntity;
import org.flickit.assessment.data.jpa.kit.tag.AssessmentKitTagJpaEntity;
import org.flickit.assessment.data.jpa.kit.user.UserJpaEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "baseinfo_assessmentkit")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentKitJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "summary", nullable = false)
    private String summary;

    @Column(name = "creation_time", nullable = false)
    private LocalDateTime creationTime;

    @Column(name = "last_modification_date", nullable = false)
    private LocalDateTime lastModificationTime;

    @Column(name = "expert_group_id", nullable = false)
    private Long expertGroupId;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive;

    @Column(name = "is_private", nullable = false)
    private Boolean isPrivate;

    @Column(name = "about", nullable = false)
    private String about;

    @ManyToMany
    @JoinTable(
        name = "fak_kit_user_access",
        joinColumns = @JoinColumn(name = "kit_id"),
        inverseJoinColumns = @JoinColumn(name = "user_id"))
    private Set<UserJpaEntity> accessGrantedUsers;

    @ManyToMany
    @JoinTable(
        name = "baseinfo_assessmentkittag_assessmentkits",
        joinColumns = @JoinColumn(name = "assessmentkit_id"),
        inverseJoinColumns = @JoinColumn(name = "assessmentkittag_id")
    )
    private Set<AssessmentKitTagJpaEntity> tags;

    @OneToMany(mappedBy = "kit")
    private List<AssessmentKitLikeJpaEntity> likes;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String TITLE = "title";
    }
}
