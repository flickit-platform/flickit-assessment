package org.flickit.flickitassessmentcore.application.port.in.evidence;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Value;
import org.flickit.flickitassessmentcore.application.domain.crud.PaginatedResponse;
import org.flickit.flickitassessmentcore.common.SelfValidating;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.flickitassessmentcore.common.ErrorMessageKey.*;

public interface GetEvidenceListUseCase {

    PaginatedResponse<EvidenceListItem> getEvidenceList(Param param);

    @Value
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @Min(value = 10, message = GET_EVIDENCE_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_EVIDENCE_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_EVIDENCE_LIST_PAGE_MIN)
        int page;

        public Param(Long questionId, UUID assessmentId, int size, int page) {
            this.questionId = questionId;
            this.assessmentId = assessmentId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record EvidenceListItem(
        UUID id,
        String description,
        long createdById,
        UUID assessmentId,
        LocalDateTime lastModificationTime
    ){}
}
