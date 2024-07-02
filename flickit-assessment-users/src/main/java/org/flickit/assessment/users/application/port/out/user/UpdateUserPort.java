package org.flickit.assessment.users.application.port.out.user;

import java.util.UUID;

public interface UpdateUserPort {

    void updateUser(Param param);

    record Param(UUID userId, String displayName, String bio, String linkedin) {}
}
