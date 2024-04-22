package org.flickit.assessment.users.application.port.out.user;

import java.util.UUID;

public interface LoadUserPort {

    UUID loadUserIdByEmail(String email);
}
