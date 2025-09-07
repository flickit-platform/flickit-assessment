package org.flickit.assessment.core.application.port.out.attributevalue;

import org.flickit.assessment.common.application.domain.advice.AttributeLevelTarget;
import org.flickit.assessment.core.application.domain.AttributeValue;
import org.flickit.assessment.core.application.port.out.attribute.LoadAttributesPort;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeValuePort {

    AttributeValue load(UUID assessmentResultId, Long attributeId);

    List<AttributeValue> load(UUID assessmentResultId, List<Long> attributeIds);

    List<AttributeValue> loadAll(UUID assessmentResultId);

    /**
     * Loads the current and target maturity level indices for attributes that are associated with
     * the given assessment and listed in {@code attributeLevelTargets}.
     *
     * @param assessmentId          The unique identifier of the assessment.
     * @param attributeLevelTargets A list of attribute level targets containing information about the desired maturity levels.
     * @return A list of {@link LoadAttributesPort.Result} containing the attribute ID, current maturity level index, and target maturity level index.
     */
    List<AttributeLevelIndex> loadCurrentAndTargetLevelIndices(UUID assessmentId, List<AttributeLevelTarget> attributeLevelTargets);

    record AttributeLevelIndex(
        long attributeId,
        int currentMaturityLevelIndex,
        int targetMaturityLevelIndex) {
    }
}
