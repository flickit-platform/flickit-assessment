package org.flickit.assessment.core.application.port.out.attributeinsight;

import org.flickit.assessment.core.application.domain.AttributeInsight;

public interface UpdateAttributeInsightPort {

    /**
     * Updates the AI insight information for a given attribute within an assessment result.
     * The following fields are updated:
     * <ul>
     *     <li>{@code aiInsight}: The AI-generated insight for the attribute.</li>
     *     <li>{@code aiInsightTime}: The timestamp when the AI insight was generated.</li>
     *     <li>{@code aiInputPath}: The file path or location of the input data used by the AI to generate the insight.</li>
     *     <li>{@code assessorInsight}: The assessorInsight is set to <strong>null</strong></li>
     *     <li>{@code assessorInsightTime}: The assessorInsightTime is set to <strong>null</strong></li>
     * </ul>
     * @param attributeInsight The {@link AttributeInsight} object
     */
    void updateAiInsight(AttributeInsight attributeInsight);

    void updateAssessorInsight(AttributeInsight attributeInsight);
}
