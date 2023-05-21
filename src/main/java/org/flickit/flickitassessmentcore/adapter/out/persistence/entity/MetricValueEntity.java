package org.flickit.flickitassessmentcore.adapter.out.persistence.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.GenericGenerator;

import java.util.UUID;

@Entity
@Table(name = "assessment_metricvalue")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class MetricValueEntity {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "assessment_result_id", referencedColumnName = "id", nullable = false)
    private AssessmentResultEntity assessmentResult;
    @Column(name = "metric_id", nullable = false)
    private Long metricId;
    @Column(name = "answer_id")
    private Long answerId;

    @Override
    public String toString() {
        return id.toString();
    }
}
