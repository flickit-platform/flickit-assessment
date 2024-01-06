package org.flickit.assessment.kit.application.port.in.assessmentkit;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetAssessmentKitsListUseCase {

    PaginatedResponse<KitsListItem> getKitsList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_LIST_IS_PRIVATE_NOT_NULL)
        Boolean isPrivate;

        @Min(value = 0, message = GET_KIT_USER_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_KIT_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_KIT_LIST_SIZE_MAX)
        int size;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;
    }

    record KitsListItem(
        Long id,
        String title,
        String summary,
        List<KitsListItemTag> tags,
        KitsListItemExpertGroup expertGroup,
        int likesNumber,
        int numberOfAssessments,
        boolean isPrivate
    ) {}

    record KitsListItemTag(Long id, String code, String title) {}

    record KitsListItemExpertGroup(Long id, String name, String picture) {}
}
