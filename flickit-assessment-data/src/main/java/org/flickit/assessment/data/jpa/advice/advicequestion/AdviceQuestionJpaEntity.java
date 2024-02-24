package org.flickit.assessment.data.jpa.advice.advicequestion;

import jakarta.persistence.*;
import lombok.*;
import org.flickit.assessment.data.jpa.advice.advice.AdviceJpaEntity;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "faa_advice_question")
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class AdviceQuestionJpaEntity {

    @Id
    @EqualsAndHashCode.Include
    @GeneratedValue(strategy = GenerationType.UUID)
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id")
    private UUID id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "advice_id", referencedColumnName = "id", nullable = false)
    private AdviceJpaEntity advice;

    @Column(name = "question_id", nullable = false)
    private Long questionId;

    @Column(name = "recommended_option_index", nullable = false)
    private Integer recommendedOptionIndex;

    @Column(name = "benefit", nullable = false)
    private Double benefit;
}
