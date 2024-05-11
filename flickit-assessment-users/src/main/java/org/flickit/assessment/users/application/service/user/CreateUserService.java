package org.flickit.assessment.users.application.service.user;

import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class CreateUserService implements CreateUserUseCase {

    @Override
    public Result createUser(Param param) {
        return null;
    }
}
