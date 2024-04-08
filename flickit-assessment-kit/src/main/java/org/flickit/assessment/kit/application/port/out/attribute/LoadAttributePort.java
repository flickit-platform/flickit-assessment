package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.util.Optional;

public interface LoadAttributePort {

    Optional<Attribute> loadByIdAndKitId(Long attributeId, Long kitId);
}
