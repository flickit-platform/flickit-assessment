package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.core.application.domain.Evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadEvidencePort {

    Evidence loadNotDeletedEvidence(UUID id);

    Result loadEvidenceWithDetails(UUID id);

    record Result(UUID id, String description, UUID assessmentId,  Questionnaire questionnaire, Question question, Answer answer,
                  UUID createdBy, LocalDateTime creationTime, LocalDateTime lastModificationTime) {

        record Questionnaire(Long id, String title){}

        record Answer(AnswerOption answerOption, Boolean isNotApplicable, ConfidenceLevel ConfidenceLevel){

            record AnswerOption(Long id, String title, Integer index){}
            record ConfidenceLevel(Long id, String title) {}
        }
    }
    record Question(Long id, String title, Integer index){}
}
