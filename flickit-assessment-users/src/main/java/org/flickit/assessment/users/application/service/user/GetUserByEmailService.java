package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.GetUserByEmailUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserByEmailService implements GetUserByEmailUseCase {

    private final LoadUserPort loadUserPort;

    @Override
    public UUID getUserByEmail(Param param) {
        return loadUserPort.loadUserIdByEmail(param.getEmail());
    }
}
