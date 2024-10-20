package org.flickit.assessment.core.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AnswerOption {

    private final Long id;
    private final Integer index;
    private final String title;
    private final long questionId;
    private final List<AnswerOptionImpact> impacts;
}
