package org.flickit.assessment.kit.application.port.out.kitversion;

import org.flickit.assessment.kit.application.domain.KitVersionStatus;

public interface CheckKitVersionExistencePort {

    boolean exists(long kitId, KitVersionStatus status);
}
