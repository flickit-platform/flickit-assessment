package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final CreateUserPort port;

    @Override
    public Result createUser(Param param) {
        User user = new User(param.getUserId(),
            param.getEmail(),
            param.getDisplayName(),
            null,
            null,
            null);

        UUID userId = port.createUser(user);
        return new Result(userId);
    }
}
