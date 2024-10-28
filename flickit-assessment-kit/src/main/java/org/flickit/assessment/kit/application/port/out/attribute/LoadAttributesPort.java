package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.AttributeWithSubject;

public interface LoadAttributesPort {

    PaginatedResponse<AttributeWithSubject> loadByKitVersionId(long kitVersionId, int size, int page);
}
