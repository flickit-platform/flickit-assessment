package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserProfilePort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class GetUserProfileService implements GetUserProfileUseCase {

    private static final Duration EXPIRY_DURATION = Duration.ofDays(1);

    private final LoadUserProfilePort port;
    private final CreateFileDownloadLinkPort createFileDownloadLinkPort;

    @Override
    public User getUserProfile(Param param) {
        User user = port.loadUserProfile(param.getCurrentUserId());

        String pictureLink = user.getPicture();
        if (pictureLink != null && !pictureLink.trim().isBlank()) {
            pictureLink =createFileDownloadLinkPort.createDownloadLink(pictureLink, EXPIRY_DURATION);
            user.setPicture(pictureLink);
        }
        return user;
    }
}
