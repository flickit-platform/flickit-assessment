package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.util.List;

public interface LoadAllAttributesPort {

    List<Attribute> loadAllByIdsAndKitVersionId(List<Long> attributeIds, long kitVersionId);
}
