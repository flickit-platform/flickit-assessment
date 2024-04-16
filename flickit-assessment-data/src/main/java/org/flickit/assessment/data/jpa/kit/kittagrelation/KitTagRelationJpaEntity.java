package org.flickit.assessment.data.jpa.kit.kittagrelation;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.AbstractEntity;


@Entity
@Table(name = "fak_kit_tag_relation")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(KitTagRelationJpaEntity.KitTagRelationKey.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class KitTagRelationJpaEntity extends AbstractEntity<KitTagRelationJpaEntity.KitTagRelationKey> {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "tag_id", nullable = false)
    private Long tagId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Override
    public KitTagRelationKey getId() {
        return new KitTagRelationKey(tagId, kitId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KitTagRelationKey {
        private Long tagId;
        private Long kitId;
    }
}
