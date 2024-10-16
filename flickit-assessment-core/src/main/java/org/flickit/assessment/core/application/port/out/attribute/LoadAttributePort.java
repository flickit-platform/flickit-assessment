package org.flickit.assessment.core.application.port.out.attribute;

import org.flickit.assessment.core.application.domain.Attribute;

public interface LoadAttributePort {

    Attribute load(Long attributeId, Long kitVersionId);
}
