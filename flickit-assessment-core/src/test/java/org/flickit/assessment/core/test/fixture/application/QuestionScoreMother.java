package org.flickit.assessment.core.test.fixture.application;

import org.flickit.assessment.core.application.port.in.attribute.GetAttributeScoreDetailUseCase;

public class QuestionScoreMother {

    public static GetAttributeScoreDetailUseCase.QuestionScore questionWithScore(int weight, double score) {
        return new GetAttributeScoreDetailUseCase.QuestionScore(
            "title",
            1,
            "Do you have CI/CD?",
            weight,
            2,
            "Yes",
            false,
            score,
            weight * score,
            1);
    }

    public static GetAttributeScoreDetailUseCase.QuestionScore questionWithoutAnswer(int weight) {
        return new GetAttributeScoreDetailUseCase.QuestionScore(
            "title",
            1,
            "Do you have CI/CD?",
            weight,
            null,
            null,
            false,
            null,
            0.0,
            1);
    }

    public static GetAttributeScoreDetailUseCase.QuestionScore questionMarkedAsNotApplicable() {
        return new GetAttributeScoreDetailUseCase.QuestionScore(
            "title",
            1,
            "Do you have CI/CD?",
            1,
            null,
            null,
            true,
            null,
            0.0,
            1);
    }
}
