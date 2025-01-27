package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class AnswerOption {

    private final long id;
    private final String title;
    private final int index;
    private final Long answerRangeId;
    private final double value;
}
