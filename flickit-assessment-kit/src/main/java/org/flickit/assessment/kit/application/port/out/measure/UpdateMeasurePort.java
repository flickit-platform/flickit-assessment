package org.flickit.assessment.kit.application.port.out.measure;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.MeasureTranslation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateMeasurePort {

    void update(Param param);

    record Param(
        long id,
        long kitVersionId,
        String title,
        String code,
        int index,
        String description,
        Map<KitLanguage, MeasureTranslation> translations,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy) {
    }

    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(List<MeasureOrder> orders,
                            long kitVersionId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {
        public record MeasureOrder(long measureId, int index) {
        }
    }
}
