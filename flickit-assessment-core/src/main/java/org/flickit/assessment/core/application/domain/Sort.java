package org.flickit.assessment.core.application.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Sort {
    QUESTIONNAIRE_TITLE("questionnaire_title"),
    WEIGHT("weight"),
    SCORE("score"),
    FINAL_SCORE("final_score"),
    CONFIDENCE("confidence");

    private final String title;

    public static final Sort DEFAULT = QUESTIONNAIRE_TITLE;
}

