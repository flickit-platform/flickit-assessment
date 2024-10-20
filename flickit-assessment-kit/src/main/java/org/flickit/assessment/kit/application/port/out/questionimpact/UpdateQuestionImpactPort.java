package org.flickit.assessment.kit.application.port.out.questionimpact;

import java.time.LocalDateTime;
import java.util.UUID;

public interface UpdateQuestionImpactPort {

    void updateWeight(UpdateWeightParam param);

    record UpdateWeightParam(Long id,
                             Long kitVersionId,
                             int weight,
                             Long questionId,
                             LocalDateTime lastModificationTime,
                             UUID lastModifiedBy) {
    }

    void update(Param param);

    record Param(Long id,
                 Long kitVersionId,
                 int weight,
                 long attributeId,
                 long maturityLevelId,
                 LocalDateTime lastModificationTime,
                 UUID lastModifiedBy) {
    }
}
