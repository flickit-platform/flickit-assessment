package org.flickit.assessment.data.jpa.kit.kittag;

import jakarta.persistence.*;
import lombok.*;

import static lombok.AccessLevel.PRIVATE;

@Entity
@Table(name = "fak_kit_tag")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitTagJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "fak_kit_tag_id_seq")
    @SequenceGenerator(name = "fak_kit_tag_id_seq", sequenceName = "fak_kit_tag_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "code", nullable = false, unique = true)
    private String code;

    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @NoArgsConstructor(access = PRIVATE)
    public static class Fields {
        public static final String CODE = "code";
    }
}
