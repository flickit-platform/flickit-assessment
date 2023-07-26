package org.flickit.flickitassessmentcore.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode
public class QualityAttribute {

    private Long id;
    private Integer weight;

    public QualityAttribute(Long id) {
        this.id = id;
    }
}
