package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Builder;
import lombok.Value;

import java.util.List;

@Value
@Builder
public class AnswerOption {

    long id;
    long questionId;
    List<AnswerOptionImpact> impacts;
}
