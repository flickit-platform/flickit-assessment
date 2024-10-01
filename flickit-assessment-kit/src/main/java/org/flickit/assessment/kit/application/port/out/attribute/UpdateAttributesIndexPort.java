package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.kit.application.port.in.attribute.UpdateAttributesOrderUseCase.AttributeParam;

import java.util.List;

public interface UpdateAttributesIndexPort {

    void updateIndexes(long kitVersionId, List<AttributeParam> attributes);
}
