package org.flickit.assessment.users.application.port.out.user;

import org.flickit.assessment.users.application.domain.User;

import java.util.UUID;

public interface CreateUserPort {

    UUID createUser(Param param);

    record Param(UUID id, String displayName, String email) {}
}
