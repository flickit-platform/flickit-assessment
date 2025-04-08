package org.flickit.assessment.kit.application.port.in.measure;


import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateMeasureUseCase {

    void updateMeasure(Param param);

    @Value
    @EqualsAndHashCode(callSuper = true)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_MEASURE_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_MEASURE_MEASURE_ID_NOT_NULL)
        Long measureId;

        @NotNull(message = UPDATE_MEASURE_INDEX_NOT_NULL)
        Integer index;

        @NotNull(message = UPDATE_MEASURE_TITLE_NOT_NULL)
        @Size(min = 3, message = UPDATE_MEASURE_TITLE_SIZE_MIN)
        @Size(max = 100, message = UPDATE_MEASURE_TITLE_SIZE_MAX)
        String title;

        @NotNull(message = UPDATE_MEASURE_DESCRIPTION_NOT_NULL)
        @Size(min = 3, message = UPDATE_MEASURE_DESCRIPTION_SIZE_MIN)
        @Size(max = 500, message = UPDATE_MEASURE_DESCRIPTION_SIZE_MAX)
        String description;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId,
                     Long measureId,
                     Integer index,
                     String title,
                     String description,
                     UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.measureId = measureId;
            this.index = index;
            this.title = title != null && !title.isBlank() ? title.trim() : null;
            this.description = description != null && !description.isBlank() ? description.trim() : null;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
