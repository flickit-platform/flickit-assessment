package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import org.flickit.assessment.kit.application.port.out.questionnaire.LoadQuestionnairePort;

import java.util.List;

public record GetQuestionnaireResponseDto(
    int questionsCount,
    List<String> relatedSubjects,
    String description,
    List<LoadQuestionnairePort.Result.Question> questions
) {}
