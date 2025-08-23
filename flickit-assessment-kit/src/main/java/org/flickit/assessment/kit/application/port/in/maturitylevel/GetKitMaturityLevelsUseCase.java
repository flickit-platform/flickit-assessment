package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MaturityLevelTranslation;

import java.util.Map;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitMaturityLevelsUseCase {

    PaginatedResponse<MaturityLevelListItem> getKitMaturityLevels(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_MATURITY_LEVELS_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @Min(value = 1, message = GET_KIT_MATURITY_LEVELS_SIZE_MIN)
        @Max(value = 100, message = GET_KIT_MATURITY_LEVELS_SIZE_MAX)
        int size;

        @Min(value = 0, message = GET_KIT_MATURITY_LEVELS_PAGE_MIN)
        int page;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, int size, int page, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.size = size;
            this.page = page;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record MaturityLevelListItem(long id,
                                 int index,
                                 String title,
                                 String description,
                                 int value,
                                 Map<KitLanguage, MaturityLevelTranslation> translations) {
    }
}
