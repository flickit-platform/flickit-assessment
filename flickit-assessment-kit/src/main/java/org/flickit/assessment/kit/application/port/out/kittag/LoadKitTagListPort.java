package org.flickit.assessment.kit.application.port.out.kittag;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.KitTag;

public interface LoadKitTagListPort {

    PaginatedResponse<KitTag> load(int page, int size);
}
