package org.flickit.assessment.kit.application.port.out.user;

import java.util.UUID;

public interface LoadUserEmailByUserIdPort {

    String loadEmail(UUID userId);
}
