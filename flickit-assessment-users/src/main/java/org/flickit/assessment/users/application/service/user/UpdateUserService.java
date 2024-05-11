package org.flickit.assessment.users.application.service.user;

import jakarta.transaction.Transactional;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.UpdateUserUseCase;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class UpdateUserService implements UpdateUserUseCase {

    @Override
    public User updateUser(Param param) {
        return null;
    }
}
