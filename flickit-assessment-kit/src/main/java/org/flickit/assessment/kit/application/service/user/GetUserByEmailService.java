package org.flickit.assessment.kit.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.exception.ResourceNotFoundException;
import org.flickit.assessment.kit.application.domain.User;
import org.flickit.assessment.kit.application.port.in.user.GetUserByEmailUseCase;
import org.flickit.assessment.kit.application.port.out.user.LoadUserByEmailPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.flickit.assessment.kit.common.ErrorMessageKey.GET_USER_BY_EMAIL_EMAIL_NOT_FOUND;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserByEmailService implements GetUserByEmailUseCase {

    private final LoadUserByEmailPort loadUserByEmailPort;

    @Override
    public UUID getUserByEmail(Param param) {
        User user = loadUserByEmailPort.loadByEmail(param.getEmail())
            .orElseThrow(() -> new ResourceNotFoundException(GET_USER_BY_EMAIL_EMAIL_NOT_FOUND));
        return user.getId();
    }
}
