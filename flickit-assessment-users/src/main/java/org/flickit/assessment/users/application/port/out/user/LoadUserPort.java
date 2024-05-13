package org.flickit.assessment.users.application.port.out.user;

import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<UUID> loadUserIdByEmail(String email);
}
