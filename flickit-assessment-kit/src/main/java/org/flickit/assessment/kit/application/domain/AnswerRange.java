package org.flickit.assessment.kit.application.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AnswerRange {

    private final Long id;
    private final String title;
    private final List<AnswerOption> answerOptions;
}
