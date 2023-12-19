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

    record KitUserAccessKey(Long kit_id, UUID user_id) {}
}


