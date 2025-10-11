package org.flickit.assessment.kit.application.port.out.kitversion;

import org.flickit.assessment.kit.application.domain.KitVersion;

import java.util.Optional;

public interface LoadKitVersionPort {

    KitVersion load(long kitVersionId);

    Optional<Long> loadKitVersionIdWithUpdatingStatus(long kitId);
}
