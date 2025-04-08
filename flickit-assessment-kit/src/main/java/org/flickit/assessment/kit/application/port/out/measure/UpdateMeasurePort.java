package org.flickit.assessment.kit.application.port.out.measure;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateMeasurePort {

    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(List<MeasureOrder> orders,
                            long kitVersionId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {
        public record MeasureOrder(long measureId, int index) {
        }
    }
}
