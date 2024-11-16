package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.domain.Attribute;

import java.util.List;

public interface LoadAttributePort {

    Attribute load(Long attributeId, Long kitVersionId);

    List<Attribute> loadByKitVersionIdAndQuestionsWithoutImpact(long kitVersionId);
}
