package org.flickit.assessment.data.jpa.kit.kitlike;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "baseinfo_assessmentkitlike")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class KitLikeJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "baseinfo_profilelike_id_seq")
    @SequenceGenerator(name = "baseinfo_profilelike_id_seq", sequenceName = "baseinfo_profilelike_id_seq", allocationSize = 1)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "assessment_kit_id", nullable = false)
    private Long kitId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;
}
