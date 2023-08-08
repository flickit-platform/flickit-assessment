package org.flickit.flickitassessmentcore.domain.calculate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AnswerOption {

    private final long id;
    private final long questionId;
    private final List<AnswerOptionImpact> impacts;
}
