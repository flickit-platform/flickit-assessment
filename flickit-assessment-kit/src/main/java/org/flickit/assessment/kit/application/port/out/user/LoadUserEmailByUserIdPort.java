package org.flickit.assessment.kit.application.port.out.user;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserEmailByUserIdPort {

    Optional<String> loadEmail(UUID userId);
}
