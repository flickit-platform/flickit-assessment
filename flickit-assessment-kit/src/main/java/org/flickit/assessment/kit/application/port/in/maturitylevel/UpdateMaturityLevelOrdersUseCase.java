package org.flickit.assessment.kit.application.port.in.maturitylevel;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Value;
import org.flickit.assessment.common.application.SelfValidating;
import org.flickit.assessment.kit.application.domain.MaturityLevelOrder;

import java.util.List;
import java.util.UUID;

import static org.flickit.assessment.common.error.ErrorMessageKey.COMMON_CURRENT_USER_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_MATURITY_LEVEL_ORDERS_KIT_VERSION_ID_NOT_NULL;
import static org.flickit.assessment.kit.common.ErrorMessageKey.UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_NOT_NULL;

public interface UpdateMaturityLevelOrdersUseCase {

    void changeOrders(Param param);

    @Value
    @EqualsAndHashCode(callSuper = false)
    class Param extends SelfValidating<Param> {

        @NotNull(message = UPDATE_MATURITY_LEVEL_ORDERS_KIT_VERSION_ID_NOT_NULL)
        Long kitVersionId;

        @NotNull(message = UPDATE_MATURITY_LEVEL_ORDERS_ORDERS_NOT_NULL)
        List<MaturityLevelOrder> orders;

        @NotNull(message = COMMON_CURRENT_USER_ID_NOT_NULL)
        UUID currentUserId;

        @Builder
        public Param(Long kitVersionId, List<MaturityLevelOrder> orders, UUID currentUserId) {
            this.kitVersionId = kitVersionId;
            this.orders = orders;
            this.currentUserId = currentUserId;
            this.validateSelf();
        }
    }
}
