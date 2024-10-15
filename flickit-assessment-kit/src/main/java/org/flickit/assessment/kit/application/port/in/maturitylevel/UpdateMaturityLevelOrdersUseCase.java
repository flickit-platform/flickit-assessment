package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.*;

public interface UpdateMaturityLevelOrdersUseCase {

    void changeOrders(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_MATURITY_LEVEL_ORDERS_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotEmpty(message = UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_NOT_NULL)
        @Size(min = 2, message = UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_SIZE_MIN)
        List<MaturityLevelParam> orders;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<MaturityLevelParam> orders, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.orders = orders;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }

    @Value
    @EqualsAndHashCode(callSuper = true)
    class MaturityLevelParam extends SelfValidating<MaturityLevelParam> {

        @NotNull(message = UPDATE_MATURITY_LEVEL_ORDERS_MATURITY_LEVEL_ID_NOT_NULL)
        Long id;

        @NotNull(message = UPDATE_MATURITY_LEVEL_ORDERS_MATURITY_LEVEL_INDEX_NOT_NULL)
        @Min(value = 1, message = UPDATE_MATURITY_LEVEL_ORDERS_MATURITY_LEVEL_INDEX_MIN)
        Integer index;

        @Builder
        public MaturityLevelParam(Long id, Integer index) {
            this.id = id;
            this.index = index;
            this.validateSelf();
        }
    }
}
