package org.flickit.assessment.kit.application.port.out.question;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface UpdateQuestionPort {

    /**
     * Updates the question entity based on the provided parameters.
     */
    void update(Param param);

    record Param(Long id,
                 Long kitVersionId,
                 String code,
                 String title,
                 Integer index,
                 String hint,
                 Boolean mayNotBeApplicable,
                 Boolean advisable,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }

    /**
     * Updates the order of questions for a specific questionnaire based on the provided {@link UpdateOrderParam} object.
     *
     * @param param the {@link UpdateOrderParam} object containing the updated order of questions. The {@code UpdateOrderParam} record contains:
     * <ul>
     *   <li>{@code kitVersionId} - The ID of the kit version associated with the questions.</li>
     *   <li>{@code orders} - A list of {@link UpdateOrderParam.QuestionOrder} records representing the question IDs and their updated indices.</li>
     *   <li>{@code questionnaireId} - The ID of the questionnaire whose question order is being updated.</li>
     *   <li>{@code lastModificationTime} - The timestamp of the last modification.</li>
     *   <li>{@code lastModifiedBy} - The UUID of the user who last modified the order.</li>
     * </ul>
     */
    void updateOrders(UpdateOrderParam param);

    record UpdateOrderParam(long kitVersionId,
                            List<QuestionOrder> orders,
                            long questionnaireId,
                            LocalDateTime lastModificationTime,
                            UUID lastModifiedBy) {

        public record QuestionOrder(long questionId, int index, String code) {
        }
    }
}
