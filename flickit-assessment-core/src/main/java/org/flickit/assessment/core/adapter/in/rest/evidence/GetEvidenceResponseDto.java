package org.flickit.assessment.core.adapter.in.rest.evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public record GetEvidenceResponseDto(UUID id,
                                     String description,
                                     Questionnaire questionnaire,
                                     Question question,
                                     Answer answer,
                                     String createdBy,
                                     LocalDateTime creationTime,
                                     LocalDateTime lastModificationTime) {

    public record Question(Long id, String title, Integer index) {
    }

    public record Questionnaire(Long id, String title) {
    }

    public record Answer(AnswerOption answerOption, ConfidenceLevel ConfidenceLevel, Boolean isNotApplicable) {
    }

    public record AnswerOption(Long id, String title, Integer index) {
    }

    public record ConfidenceLevel(Integer id, String title) {
    }
}
