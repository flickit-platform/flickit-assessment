package org.flickit.assessment.kit.application.port.out.attribute;

public interface DeleteAttributePort {

    void delete(long attributeId, long kitVersionId);
}
