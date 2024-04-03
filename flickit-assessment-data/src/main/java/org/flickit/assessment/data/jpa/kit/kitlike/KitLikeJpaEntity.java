package org.flickit.assessment.data.jpa.kit.kitlike;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.AbstractEntity;

import java.util.UUID;

@Entity
@Table(name = "fak_kit_like")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@IdClass(KitLikeJpaEntity.KitLikeKey.class)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, callSuper = false)
public class KitLikeJpaEntity extends AbstractEntity<KitLikeJpaEntity.KitLikeKey> {

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "assessment_kit_id", nullable = false)
    private Long assessmentKitId;

    @Id
    @EqualsAndHashCode.Include
    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Override
    public KitLikeKey getId() {
        return new KitLikeKey(assessmentKitId, userId);
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KitLikeKey {
        private Long assessmentKitId;
        private UUID userId;
    }
}
