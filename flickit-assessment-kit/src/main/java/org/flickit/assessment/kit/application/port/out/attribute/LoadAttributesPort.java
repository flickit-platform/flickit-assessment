package org.flickit.assessment.kit.application.port.out.attribute;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.attribute.GetAttributesUseCase.AttributeListItem;

public interface LoadAttributesPort {

    PaginatedResponse<AttributeListItem> loadByKitVersionId(long kitVersionId, int size, int page);
}
