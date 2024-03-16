package org.flickit.assessment.data.jpa.kit.kittag;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "baseinfo_assessmentkittag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitTagJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_profiletag_id_seq")
    @SequenceGenerator(name = "baseinfo_profiletag_id_seq", sequenceName = "baseinfo_profiletag_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "title", nullable = false)
    private String title;
}
