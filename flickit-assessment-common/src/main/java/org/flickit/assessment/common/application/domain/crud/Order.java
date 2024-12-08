package org.flickit.assessment.common.application.domain.crud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum Order {
    QUESTIONNAIRE_TITLE("questionnaireTitle"),
    WEIGHT("weight" ),
    SCORE("score" ),
    FINAL_SCORE("finalScore"  ),
    CONFIDENCE("confidence"  ),;

    public static final Order DEFAULT = WEIGHT;

    private final String title;
}
