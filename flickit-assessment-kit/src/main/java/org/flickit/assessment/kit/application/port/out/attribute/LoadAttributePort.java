package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.domain.Attribute;

public interface LoadAttributePort {

    Attribute load(Long attributeId, Long kitVersionId);
}
