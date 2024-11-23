package org.flickit.assessment.kit.application.port.in.kitcustom;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitCustomSubjectUseCase {

    PaginatedResponse<Result> getKitCustomSubject(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = GET_KIT_CUSTOM_SUBJECT_KIT_ID_NOT_NULL)
        Long kitId;

        Long kitCustomId;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Min(value = 0, message = GET_KIT_CUSTOM_SUBJECT_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_KIT_CUSTOM_SUBJECT_SIZE_MIN)
        @Max(value = 100, message = GET_KIT_CUSTOM_SUBJECT_SIZE_MAX)
        int size;

        @Builder
        public Param(Long kitId, Long kitCustomId, UUID currentUserId, int page, int size) {
            this.kitId = kitId;
            this.kitCustomId = kitCustomId;
            this.page = page;
            this.size = size;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    record Result(Subject subject) {

        public record Subject(long id, String title, Weight weight, List<Attribute> attributes) {

            public record Attribute(long id, String title, Weight weight) {}
        }

        public record Weight(int defaultValue, Integer customValue) {}
    }
}
