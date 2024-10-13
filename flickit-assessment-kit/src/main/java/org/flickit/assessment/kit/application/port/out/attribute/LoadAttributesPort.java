package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.Subject;

public interface LoadAttributesPort {

    PaginatedResponse<Subject> loadByKitVersionId(long kitVersionId, int size, int page);
}
