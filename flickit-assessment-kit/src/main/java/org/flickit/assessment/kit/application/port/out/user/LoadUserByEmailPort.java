package org.flickit.assessment.kit.application.port.out.user;

import org.flickit.assessment.kit.application.domain.User;

import java.util.Optional;

public interface LoadUserByEmailPort {

    Optional<User> loadByEmail(String email);
}
