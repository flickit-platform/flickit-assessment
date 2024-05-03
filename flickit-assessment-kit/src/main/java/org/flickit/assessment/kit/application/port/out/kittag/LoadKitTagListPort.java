package org.flickit.assessment.kit.application.port.out.kittag;

import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

public interface LoadKitTagListPort {

    List<KitTag> loadByKitId(long kitId);

    PaginatedResponse<KitTag> loadAll(int page, int size);

    List<Result> loadByKitIds(List<Long> kitIds);

    record Result(long kitId, List<KitTag> kitTags) {
    }
}
