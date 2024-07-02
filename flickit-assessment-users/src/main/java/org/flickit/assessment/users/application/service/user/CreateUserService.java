package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.CreateUserUseCase;
import org.flickit.assessment.users.application.port.out.user.CreateUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
@RequiredArgsConstructor
public class CreateUserService implements CreateUserUseCase {

    private final CreateUserPort createUserPort;

    @Override
    public Result createUser(Param param) {
        UUID userId = createUserPort.createUser(toParam(param.getUserId(), param.getDisplayName(), param.getEmail()));
        return new Result(userId);
    }

    private CreateUserPort.Param toParam(UUID userId, String displayName, String email) {
        return new CreateUserPort.Param(userId, displayName, email);
    }
}
