package org.flickit.assessment.data.jpa.kit.assessmentkitversion;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fak_assessment_kit_version")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AssessmentKitVersionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_assessment_kit_version_id_seq")
    @SequenceGenerator(name = "fak_assessment_kit_version_id_seq", sequenceName = "fak_assessment_kit_version_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Column(name = "version_status", nullable = false)
    private VersionStatus versionStatus;

}

enum VersionStatus {
    ACTIVE(1), UPDATING(2), ARCHIVE(3);

    final int code;

    VersionStatus(int code) {
        this.code = code;
    }
}
