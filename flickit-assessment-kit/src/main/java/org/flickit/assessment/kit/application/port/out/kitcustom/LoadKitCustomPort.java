package org.flickit.assessment.kit.application.port.out.kitcustom;

import org.flickit.assessment.kit.application.domain.KitCustomData;

public interface LoadKitCustomPort {

    Result loadById(long kitCustomId, long kitId);

    record Result(long id, String title, long kitId, KitCustomData customData) {}
}
