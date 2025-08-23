package org.flickit.assessment.kit.application.port.out.kitversion;

import org.flickit.assessment.kit.application.domain.KitVersionStatus;

import java.util.UUID;

public interface CreateKitVersionPort {

    long persist(Param param);

    record Param(long kitId, KitVersionStatus status, UUID createdBy) {
    }
}
