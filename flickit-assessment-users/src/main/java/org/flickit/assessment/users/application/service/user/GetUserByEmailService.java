package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.GetUserByEmailUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserByEmailService implements GetUserByEmailUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public Result getUserByEmail(Param param) {
        LoadUserPort.Result userResult = loadUserPort.loadFullUserByEmail(param.getEmail());

        return new Result(userResult.user(),
            userResult.lastLogin(),
            userResult.isSuperUser(),
            userResult.isStaff(),
            userResult.isActive(),
            userResult.password());
    }
}
