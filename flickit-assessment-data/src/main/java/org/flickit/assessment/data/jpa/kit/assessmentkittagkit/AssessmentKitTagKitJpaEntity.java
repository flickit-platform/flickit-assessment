package org.flickit.assessment.data.jpa.kit.assessmentkittagkit;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "baseinfo_assessmentkittag_assessmentkits")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentKitTagKitJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_profiletag_profiles_id_seq")
    @SequenceGenerator(name = "baseinfo_profiletag_profiles_id_seq", sequenceName = "baseinfo_profiletag_profiles_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "assessmentkittag_id")
    private Long tagId;

    @Column(name = "assessmentkit_id")
    private Long kitId;
}
