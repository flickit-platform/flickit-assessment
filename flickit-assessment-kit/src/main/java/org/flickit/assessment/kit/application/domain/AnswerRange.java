package org.flickit.assessment.kit.application.domain;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@RequiredArgsConstructor
public class AnswerRange {

    private final Long id;
    private final String title;
    @Setter
    @EqualsAndHashCode.Exclude private List<AnswerOption> answerOptions;
}
