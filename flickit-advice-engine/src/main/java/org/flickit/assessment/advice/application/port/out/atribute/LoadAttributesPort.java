package org.flickit.assessment.advice.application.port.out.atribute;

import org.flickit.assessment.advice.application.domain.Attribute;

import java.util.List;
import java.util.UUID;

public interface LoadAttributesPort {

    List<Attribute> loadByIdsAndKitVersionId(List<Long> attributeIds, UUID assessmentId);
}
