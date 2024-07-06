package org.flickit.assessment.core.application.port.out.evidence;

import org.flickit.assessment.core.application.domain.Evidence;

import java.time.LocalDateTime;
import java.util.UUID;

public interface LoadEvidencePort {

    Evidence loadNotDeletedEvidence(UUID id);

    Result loadEvidenceWithDetails(UUID id);

    record Result(UUID id, String description, UUID assessmentId, EvidenceQuestionnaire evidenceQuestionnaire, EvidenceQuestion evidenceQuestion,
                  EvidenceAnswer evidenceAnswer, String createdBy, LocalDateTime creationTime, LocalDateTime lastModificationTime) {

        public record EvidenceQuestion(Long id, String title, Integer index){}
        public record EvidenceQuestionnaire(Long id, String title){}
        public record EvidenceAnswer(EvidenceAnswerOption evidenceAnswerOption, Integer confidenceLevel, Boolean isNotApplicable){}
        public record EvidenceAnswerOption(Long id, String title, Integer index){}
    }
}
