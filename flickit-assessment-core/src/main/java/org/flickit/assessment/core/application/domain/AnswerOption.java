package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AnswerOption {

    private final long id;
    private final Long questionId;
    private final List<AnswerOptionImpact> impacts;
}
