package org.flickit.assessment.data.jpa.kit.kittag;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
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
