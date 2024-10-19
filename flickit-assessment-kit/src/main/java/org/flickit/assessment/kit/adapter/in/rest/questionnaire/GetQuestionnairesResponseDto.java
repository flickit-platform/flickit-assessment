package org.flickit.assessment.kit.adapter.in.rest.questionnaire;

public record GetQuestionnairesResponseDto(long id, String title, int index, String description, int questionsCount) {}
