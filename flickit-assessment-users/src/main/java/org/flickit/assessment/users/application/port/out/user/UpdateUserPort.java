package org.flickit.assessment.users.application.port.out.user;

import org.flickit.assessment.users.application.domain.User;

import java.util.UUID;

public interface UpdateUserPort {

    User updateUser(Param param);

    record Param(UUID userId, String displayName, String bio, String linkedin) {}
}
