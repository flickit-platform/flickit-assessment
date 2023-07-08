package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Value;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface AddEvidenceUseCase {

    Result addEvidence(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotBlank(message = ADD_EVIDENCE_DESC_NOT_BLANK)
        @Size(min = 3, message = ADD_EVIDENCE_DESC_SIZE_MIN)
        @Size(max = 1000, message = ADD_EVIDENCE_DESC_SIZE_MAX)
        String description;

        @NotNull(message = ADD_EVIDENCE_CREATED_BY_ID_NOT_NULL)
        Long createdById;

        @NotNull(message = ADD_EVIDENCE_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = ADD_EVIDENCE_QUESTION_ID_NOT_NULL)
        Long questionId;

        public Param(String description, Long createdById, UUID assessmentId, Long questionId) {
            this.description = description;
            this.createdById = createdById;
            this.assessmentId = assessmentId;
            this.questionId = questionId;
            this.validateSelf();
        }
    }

    record Result(UUID id){}
}
