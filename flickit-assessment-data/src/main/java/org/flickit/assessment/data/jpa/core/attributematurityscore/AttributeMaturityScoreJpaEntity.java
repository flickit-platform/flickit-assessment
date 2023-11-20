package org.flickit.assessment.data.jpa.core.attributematurityscore;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.UUID;

@Entity
@IdClass(AttributeMaturityScoreJpaEntity.EntityId.class)
@Table(name = "fac_attribute_maturity_score")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeMaturityScoreJpaEntity {

    @Id
    @Column(name = "attribute_value_id", nullable = false)
    private UUID attributeValueId;

    @Id
    @Column(name = "maturity_level_id", nullable = false)
    private Long maturityLevelId;

    @Column(name = "score")
    private Double score;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class EntityId implements Serializable {
        private UUID attributeValueId;
        private Long maturityLevelId;
    }
}
