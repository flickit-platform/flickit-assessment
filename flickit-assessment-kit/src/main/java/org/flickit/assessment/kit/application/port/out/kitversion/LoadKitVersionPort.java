package org.flickit.assessment.kit.application.port.out.kitversion;

import org.flickit.assessment.kit.application.domain.KitVersion;

public interface LoadKitVersionPort {

    KitVersion load(long kitVersionId);
}
