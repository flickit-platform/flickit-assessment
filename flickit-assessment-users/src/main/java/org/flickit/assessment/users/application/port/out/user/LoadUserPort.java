package org.flickit.assessment.users.application.port.out.user;

import org.flickit.assessment.users.application.domain.User;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<UUID> loadUserIdByEmail(String email);

    User loadUser(UUID id);
}
