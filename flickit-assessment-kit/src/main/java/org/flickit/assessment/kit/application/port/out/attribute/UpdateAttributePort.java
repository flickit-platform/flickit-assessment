package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.kit.KitLanguage;
import org.flickit.assessment.common.application.domain.kit.translation.AttributeTranslation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface UpdateAttributePort {

    void update(Param param);

    record Param(
        long id,
        long kitVersionId,
        String code,
        String title,
        int index,
        String description,
        int weight,
        Map<KitLanguage, AttributeTranslation> translations,
        LocalDateTime lastModificationTime,
        UUID lastModifiedBy,
        long subjectId) {
    }

    /**
     * Updates the order of attributes within a kit version based on the provided parameters.
     *
     * @param param the parameters used to update the orders, including the list of attribute orders,
     *              the kit version ID, last modification time, and the ID of the last user who modified it.
     */
    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(List<AttributeOrder> orders,
                            long kitVersionId,
                            long subjectId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {
        public record AttributeOrder(long attributeId, int index) {
        }
    }
}
