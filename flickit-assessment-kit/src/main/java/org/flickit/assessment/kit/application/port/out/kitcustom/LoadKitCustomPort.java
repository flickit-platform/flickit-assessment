package org.flickit.assessment.kit.application.port.out.kitcustom;

import org.flickit.assessment.kit.application.domain.KitCustom;
import org.flickit.assessment.common.application.domain.kitcustom.KitCustomData;

public interface LoadKitCustomPort {

    Result loadByIdAndKitId(long kitCustomId, long kitId);

    record Result(long id, String title, long kitId, KitCustomData customData) {}

    KitCustom load(long kitCustomId);
}
