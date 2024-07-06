package org.flickit.assessment.users.application.port.out.user;

import org.flickit.assessment.users.application.domain.User;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

public interface LoadUserPort {

    Optional<UUID> loadUserIdByEmail(String email);

    User loadUser(UUID id);

    Result loadUserByEmail(String email);

    record Result(
        User user,
        LocalDateTime lastLogin,
        boolean isSuperUser,
        boolean isStaff,
        boolean isActive,
        String password) {}
}
