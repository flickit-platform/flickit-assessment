package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class QualityAttribute {

    Long id;
    Integer weight;
    List<Question> questions;
}
