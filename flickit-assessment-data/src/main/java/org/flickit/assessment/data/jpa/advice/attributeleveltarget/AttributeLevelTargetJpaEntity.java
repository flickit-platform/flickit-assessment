package org.flickit.assessment.data.jpa.advice.attributeleveltarget;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "faa_attribute_level_target")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AttributeLevelTargetJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @ManyToOne(optional = false)
    @Column(name = "advice_id", nullable = false)
    private AdviceJpaEntity advice;

    @Column(name = "attribute_id", nullable = false)
    private Long attributeId;

    @Column(name = "maturity_level_id", nullable = false)
    private Long maturityLevelId;
}
