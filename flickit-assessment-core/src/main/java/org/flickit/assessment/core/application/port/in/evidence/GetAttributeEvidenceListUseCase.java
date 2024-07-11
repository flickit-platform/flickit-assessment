package org.flickit.assessment.core.application.port.in.evidence;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.validation.EnumValue;
import org.flickit.assessment.core.application.domain.EvidenceType;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.core.common.ErrorMessageKey.*;

public interface GetAttributeEvidenceListUseCase {

    PaginatedResponse<AttributeEvidenceListItem> getAttributeEvidenceList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<GetEvidenceListUseCase.Param> {

        @NotNull(message = GET_ATTRIBUTE_EVIDENCE_LIST_ASSESSMENT_ID_NOT_NULL)
        UUID assessmentId;

        @NotNull(message = GET_ATTRIBUTE_EVIDENCE_LIST_ATTRIBUTE_ID_NOT_NULL)
        Long attributeId;

        @NotNull(message = GET_ATTRIBUTE_EVIDENCE_LIST_TYPE_NOT_NULL)
        @EnumValue(enumClass = EvidenceType.class, message = GET_ATTRIBUTE_EVIDENCE_LIST_TYPE_INVALID)
        String type;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 1, message = GET_ATTRIBUTE_EVIDENCE_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_ATTRIBUTE_EVIDENCE_LIST_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_ATTRIBUTE_EVIDENCE_LIST_PAGE_MIN)
        int page;

        public Param(UUID assessmentId, Long attributeId, String type, UUID currentUserId, int size, int page) {
            this.assessmentId = assessmentId;
            this.attributeId = attributeId;
            this.type = type;
            this.currentUserId = currentUserId;
            this.size = size;
            this.page = page;
            this.validateSelf();
        }
    }

    record AttributeEvidenceListItem(String description, Long attachmentsCount) {}
}
