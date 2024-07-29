package org.flickit.assessment.core.adapter.in.rest.evidence;

import org.flickit.assessment.core.application.domain.*;
import org.flickit.assessment.core.application.port.in.evidence.GetEvidenceUseCase;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record GetEvidenceResponseDto(EvidenceDto evidence, QuestionDto question) {

    public record EvidenceDto(
        UUID id,
        String description,
        String type,
        User createdBy,
        LocalDateTime creationTime,
        LocalDateTime lastModificationTime) {

        public static EvidenceDto of(Evidence evidence, User user) {
            return new EvidenceDto(
                evidence.getId(),
                evidence.getDescription(),
                (evidence.getType() != null) ? EvidenceType.values()[evidence.getType()].getTitle() : null,
                user,
                evidence.getCreationTime(),
                evidence.getLastModificationTime()
            );
        }
    }

    public record QuestionDto(
        Long id,
        String title,
        int index,
        List<OptionDto> options,
        QuestionnaireDto questionnaire,
        GetEvidenceUseCase.QuestionAnswer answer) {

        public static QuestionDto of(Question question, GetEvidenceUseCase.QuestionAnswer answer) {
            return new QuestionDto(
                question.getId(),
                question.getTitle(),
                question.getIndex(),
                question.getOptions().stream().map(OptionDto::of).toList(),
                QuestionnaireDto.of(question.getQuestionnaire()),
                answer
            );
        }
    }

    public record OptionDto(Long id, int index, String title) {

        public static OptionDto of(AnswerOption option) {
            return new OptionDto(option.getId(), option.getIndex(), option.getTitle());
        }
    }

    public record QuestionnaireDto(Long id, String title) {

        public static QuestionnaireDto of(Questionnaire questionnaire) {
            return new QuestionnaireDto(questionnaire.getId(), questionnaire.getTitle());
        }
    }
}
