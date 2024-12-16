package org.flickit.assessment.core.application.domain;

import jakarta.annotation.Nullable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.UUID;

@Getter
@RequiredArgsConstructor
public class Answer {

    private final UUID id;

    @Nullable
    private final AnswerOption selectedOption;

    private final Long questionId;

    private final Integer confidenceLevelId;

    private final Boolean isNotApplicable;
}
