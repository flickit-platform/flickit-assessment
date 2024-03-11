package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.exception.ResourceNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetEvidenceListUseCase {

    /**
     * @throws ResourceNotFoundException if no assessment found by the given assessmentId
     */
    PaginatedResponse<EvidenceListItem> getEvidenceList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_EVIDENCE_LIST_QUESTION_ID_NOT_NULL)
        Long questionId;

        @NotNull(message = GET_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @Min(value = 1, message = GET_EVIDENCE_LIST_SIZE_MIN)
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
        UUID createdById,
        UUID assessmentId,
        String type,
        LocalDateTime lastModificationTime){}
}
