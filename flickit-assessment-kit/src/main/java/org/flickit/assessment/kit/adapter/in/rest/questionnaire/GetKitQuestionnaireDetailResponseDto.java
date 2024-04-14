package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

import org.flickit.assessment.kit.application.port.out.questionnaire.LoadKitQuestionnaireDetailPort;

import java.util.List;

public record GetKitQuestionnaireDetailResponseDto(
    int questionsCount,
    List<String> relatedSubjects,
    String description,
    List<LoadKitQuestionnaireDetailPort.Result.Question> questions
) {}
