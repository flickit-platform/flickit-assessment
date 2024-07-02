package org.flickit.assessment.users.application.service.user;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.port.in.user.UpdateUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.user.UpdateUserPort;
import org.springframework.stereotype.Service;

@Service
@Transactional
@RequiredArgsConstructor
public class UpdateUserProfileService implements UpdateUserProfileUseCase {

    private final UpdateUserPort updateUserPort;

    @Override
    public void updateUserProfile(Param param) {
        updateUserPort.updateUser(toParam(param));
    }

    private UpdateUserPort.Param toParam(Param param) {
        return new UpdateUserPort.Param(param.getCurrentUserId(),
            param.getDisplayName(),
            param.getBio(),
            param.getLinkedin());
    }
}
