package org.flickit.assessment.advice.application.port.out.atribute;

import org.flickit.assessment.advice.application.domain.Attribute;
import org.flickit.assessment.common.application.domain.kit.KitLanguage;

import java.util.List;
import java.util.UUID;

public interface LoadAttributesPort {

    List<Attribute> loadByIdsAndAssessmentId(List<Long> attributeIds, UUID assessmentId);

    List<Result> loadAll(UUID assessmentId, long kitVersionId, KitLanguage assessmentLanguage);

    record Result(long id,
                  MaturityLevel maturityLevel) {
    }

    record MaturityLevel(long id,
                         String title,
                         String description,
                         int index,
                         int value) {
    }
}
