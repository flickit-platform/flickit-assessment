package org.flickit.assessment.users.application.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.UpdateUserUseCase;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPort;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserService implements UpdateUserUseCase {

    private final UpdateUserPort port;

    @Override
    public void updateUser(Param param) {
        port.updateUser(toParam(param));
    }

    private UpdateUserPort.Param toParam(Param param) {
        return new UpdateUserPort.Param(param.getCurrentUserId(),
            param.getDisplayName(),
            param.getBio(),
            param.getLinkedin());
    }
}
