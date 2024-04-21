package org.flickit.assessment.kit.application.port.out.kittag;

import org.flickit.assessment.kit.application.domain.KitTag;

import java.util.List;

public interface LoadKitListTagsListPort {

    List<Result> loadByKitIds(List<Long> kitIds);

    record Result(long kitId, List<KitTag> kitTags) {
    }
}
