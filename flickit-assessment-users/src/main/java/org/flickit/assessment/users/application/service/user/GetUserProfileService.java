package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.user.LoadUserProfilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserProfileService implements GetUserProfileUseCase {

    private final LoadUserProfilePort port;

    @Override
    public User getUserProfile(Param param) {
        return port.loadUserProfile(param.getCurrentUserId());
    }
}
