package org.flickit.assessment.common.application.domain.crud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SortEnum {
    QUESTIONNAIRE_TITLE("questionnaire_title"),
    WEIGHT("weight"),
    SCORE("score"),
    FINAL_SCORE("finalScore"),
    CONFIDENCE("confidence");

    private final String title;

    public static final SortEnum DEFAULT = QUESTIONNAIRE_TITLE;
}

