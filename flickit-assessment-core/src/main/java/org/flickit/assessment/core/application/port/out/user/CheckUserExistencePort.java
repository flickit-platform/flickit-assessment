package org.flickit.assessment.core.application.port.out.user;

import java.util.UUID;

public interface CheckUserExistencePort {

    boolean existsById(UUID id);
}
