package org.flickit.assessment.kit.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@RequiredArgsConstructor
public class Question {

    private final String code;

    private final String title;

    private final String description;

    private final int index;

    private final String questionnaireCode;

    private final List<QuestionImpact> questionImpacts;

    private final List<Answer> answers;

    private final boolean mayNotBeApplicable;
}

@Getter
@RequiredArgsConstructor
class QuestionImpact {

    private final String attributeCode;

    private final Level level;

    private final String question;

    private final Map<String, Double> optionValues;

    private final int weight;
}

@Getter
@RequiredArgsConstructor
class Answer {

    private final String caption;

    private final int value;

    private final int index;
}

