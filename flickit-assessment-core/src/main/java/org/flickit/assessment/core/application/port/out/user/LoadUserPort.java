package org.flickit.assessment.core.application.port.out.user;

import org.flickit.assessment.core.application.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<User> loadById(UUID createdBy);

    Optional<User> loadByEmail(String email);

    Optional<UUID> loadSystemUserId();
}
