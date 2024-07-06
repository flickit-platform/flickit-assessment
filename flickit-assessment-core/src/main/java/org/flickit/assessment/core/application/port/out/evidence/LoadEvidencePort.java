package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.core.application.domain.Evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadEvidencePort {

    Evidence loadNotDeletedEvidence(UUID id);

    Result loadEvidenceWithDetails(UUID id);

    record Result(UUID id, String description, UUID assessmentId, Questionnaire questionnaire, Question question,
                  Answer answer,
                  String createdBy, LocalDateTime creationTime, LocalDateTime lastModificationTime) {

        public record Question(Long id, String title, Integer index){}

        public record Questionnaire(Long id, String title){}

        public record Answer(AnswerOption answerOption, Integer confidenceLevel, Boolean isNotApplicable){}

        public record AnswerOption(Long id, String title, Integer index){}
    }
}
