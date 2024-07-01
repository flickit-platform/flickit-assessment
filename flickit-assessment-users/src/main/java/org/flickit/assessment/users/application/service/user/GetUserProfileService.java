package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserProfileService implements GetUserProfileUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadUserPort loadUserPort;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public User getUserProfile(Param param) {
        User user = loadUserPort.loadUser(param.getCurrentUserId());

        if (user.getPicture() != null && !user.getPicture().trim().isBlank()) {
            String pictureLink = createFileDownloadLinkPort.createDownloadLink(user.getPicture(), EXPIRY_DURATION);
            user.setPicture(pictureLink);
        }
        return user;
    }
}
