package org.flickit.assessment.kit.application.port.out.maturitylevel;

import org.flickit.assessment.kit.application.domain.MaturityLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateMaturityLevelPort {

    void updateAll(List<MaturityLevel> maturityLevels, Long kitVersionId, UUID lastModifiedBy);

    void update(MaturityLevel maturityLevel, Long kitVersionId, LocalDateTime lastModificationTime, UUID lastModifiedBy);

    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(List<UpdateOrderParam.MaturityLevelOrder> orders,
                            long kitVersionId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {
        public record MaturityLevelOrder(long maturityLevelId, int index, int value) {
        }
    }
}
