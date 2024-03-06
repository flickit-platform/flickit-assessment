package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.domain.Attribute;

public interface CreateAttributePort {

    Long persist(Attribute attribute, Long subjectId, Long kitVersionId);
}
