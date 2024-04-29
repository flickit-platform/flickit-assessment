package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.util.List;

public interface LoadAllAttributesPort {

    List<Attribute> loadAllByIds(List<Long> attributeIds);
}
