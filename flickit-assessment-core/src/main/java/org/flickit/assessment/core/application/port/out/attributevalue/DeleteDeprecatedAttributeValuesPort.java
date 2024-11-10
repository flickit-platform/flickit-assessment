package org.flickit.assessment.core.application.port.out.attributevalue;

import java.util.UUID;

public interface DeleteDeprecatedAttributeValuesPort {

    void deleteDeprecatedAttributeValues(UUID assessmentResultId);
}
