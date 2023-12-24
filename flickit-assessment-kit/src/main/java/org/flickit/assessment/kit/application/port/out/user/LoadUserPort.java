package org.flickit.assessment.kit.application.port.out.user;

import org.flickit.assessment.kit.application.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<User> loadById(UUID userId);
}
