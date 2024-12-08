package org.flickit.assessment.common.application.domain.crud;

public enum Order {
    QUESTIONNAIRE_TITLE, WEIGHT, SCORE, FINAL_SCORE, CONFIDENCE;
    public static final Order DEFAULT = WEIGHT;
}
