package org.flickit.assessment.data.jpa.kit.kitlanguage;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.FieldNameConstants;

import java.io.Serializable;

@Entity
@IdClass(KitLanguageJpaEntity.EntityId.class)
@Table(name = "fak_kit_language")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitLanguageJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "lang_id", nullable = false)
    private Integer langId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {

        private Long kitId;
        private Integer langId;
    }
}
