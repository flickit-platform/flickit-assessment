package org.flickit.assessment.kit.application.port.out.kitversion;

import org.flickit.assessment.kit.application.domain.KitVersion;
import org.flickit.assessment.kit.application.domain.KitVersionStatus;

public interface LoadKitVersionPort {

    KitVersion load(long kitVersionId);

    Long loadKitVersionIdByStatus(long kitId, KitVersionStatus status);
}
