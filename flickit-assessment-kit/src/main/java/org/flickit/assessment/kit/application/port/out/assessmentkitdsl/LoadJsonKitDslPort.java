package org.flickit.assessment.kit.application.port.out.assessmentkitdsl;

import org.flickit.assessment.kit.application.domain.KitDsl;

import java.util.Optional;

public interface LoadJsonKitDslPort {

    Optional<KitDsl> load(Long id);
}
