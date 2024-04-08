package org.flickit.assessment.users.application.port.out.user;

import java.util.UUID;

public interface LoadUserEmailByUserIdPort {

    String loadEmail(UUID userId);
}
