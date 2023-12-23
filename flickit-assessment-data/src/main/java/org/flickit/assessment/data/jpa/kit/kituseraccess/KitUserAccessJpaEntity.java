package org.flickit.assessment.data.jpa.kit.kituseraccess;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "fak_kit_user_access")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitUserAccessJpaEntity {

    @EmbeddedId
    private KitUserAccessKey id;

    @Getter
    @Embeddable
    @AllArgsConstructor
    @EqualsAndHashCode(onlyExplicitlyIncluded = true)
    class KitUserAccessKey {

        @Column(name = "kit_id", nullable = false)
        private Long kitId;

        @Column(name = "user_id", nullable = false)
        private UUID userId;
    }
}
