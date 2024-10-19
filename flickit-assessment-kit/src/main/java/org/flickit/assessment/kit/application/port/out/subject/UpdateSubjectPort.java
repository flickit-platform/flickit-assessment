package org.flickit.assessment.kit.application.port.out.subject;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateSubjectPort {

    /**
     * Updates a subject with the provided parameters.
     *
     * @param param the parameters used to update the subject, including the subject's ID,
     *              kit version ID, code, title, index, description, weight, last modification time,
     *              and the ID of the last user who modified it.
     */
    void update(Param param);

    record Param(long id,
                 long kitVersionId,
                 String code,
                 String title,
                 int index,
                 String description,
                 int weight,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }

    /**
     * Updates the order of subjects within a kit version based on the provided parameters.
     *
     * @param param the parameters used to update the orders, including the list of subject orders,
     *              the kit version ID, last modification time, and the ID of the last user who modified it.
     */
    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(List<SubjectOrder> orders,
                            long kitVersionId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {
        public record SubjectOrder(long subjectId, int index) {
        }
    }
}
