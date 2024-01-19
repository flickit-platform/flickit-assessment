package org.flickit.assessment.data.jpa.kit.kituseraccess;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.AbstractEntity;

import java.util.UUID;

@Entity
@Table(name = "fak_kit_user_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(KitUserAccessJpaEntity.KitUserAccessKey.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class KitUserAccessJpaEntity extends AbstractEntity<KitUserAccessJpaEntity.KitUserAccessKey> {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "kit_id", nullable = false)
    private Long kitId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Override
    public KitUserAccessKey getId() {
        return new KitUserAccessKey(kitId, userId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KitUserAccessKey {
        private Long kitId;
        private UUID userId;
    }
}
