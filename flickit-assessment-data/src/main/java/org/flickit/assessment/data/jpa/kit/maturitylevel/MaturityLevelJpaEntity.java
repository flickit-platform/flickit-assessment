package org.flickit.assessment.data.jpa.kit.maturitylevel;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "baseinfo_maturitylevel")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class MaturityLevelJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_maturitylevel_id_seq")
    @SequenceGenerator(name = "baseinfo_maturitylevel_id_seq", sequenceName = "baseinfo_maturitylevel_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", length = 50)
    private String code;

    @Column(name = "title", length = 100, nullable = false)
    private String title;

    @Column(name = "value", nullable = false)
    private Integer value;

    @Column(name = "index", nullable = false)
    private Integer index;

    @Column(name = "assessment_kit_id")
    private Long assessmentKitId;
}
