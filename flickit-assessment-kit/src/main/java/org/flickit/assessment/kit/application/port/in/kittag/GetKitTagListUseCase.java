package org.flickit.assessment.kit.application.port.in.kittag;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.KitTag;

import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface GetKitTagListUseCase {

    PaginatedResponse<KitTag> getKitTagList(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @Min(value = 0, message = GET_KIT_TAG_LIST_PAGE_MIN)
        int page;

        @Min(value = 1, message = GET_KIT_TAG_LIST_SIZE_MIN)
        @Max(value = 100, message = GET_KIT_TAG_LIST_SIZE_MAX)
        int size;

        public Param(int page, int size) {
            this.page = page;
            this.size = size;
            this.validateSelf();
        }
    }
}
