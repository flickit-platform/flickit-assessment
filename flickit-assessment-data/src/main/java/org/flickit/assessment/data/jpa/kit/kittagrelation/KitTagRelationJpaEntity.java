package org.flickit.assessment.data.jpa.kit.kittagrelation;

import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "fak_kit_tag_relation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(KitTagRelationJpaEntity.KitTagRelationKey.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitTagRelationJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kittag_id", nullable = false)
    private Long tagId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KitTagRelationKey {
        private Long tagId;
        private Long kitId;
    }
}
