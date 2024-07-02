package org.flickit.assessment.users.application.port.out.user;

import java.util.UUID;

public interface CreateUserPort {

    UUID persist(UUID id, String displayName, String email);
}
