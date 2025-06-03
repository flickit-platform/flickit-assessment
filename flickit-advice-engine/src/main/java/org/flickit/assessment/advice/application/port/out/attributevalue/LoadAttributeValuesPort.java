package org.flickit.assessment.advice.application.port.out.attributevalue;

import java.util.List;
import java.util.UUID;

public interface LoadAttributeValuesPort {

    List<Result> loadAll(UUID assessmentResultId);

    record Result(long attributeId, long maturityLevelId) {
    }
}
