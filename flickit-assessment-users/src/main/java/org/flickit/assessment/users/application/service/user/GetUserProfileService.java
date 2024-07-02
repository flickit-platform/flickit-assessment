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
    public UserProfile getUserProfile(Param param) {
        User user = loadUserPort.loadUser(param.getCurrentUserId());

        String pictureLink = null;
        if (user.getPicturePath() != null && !user.getPicturePath().trim().isBlank()) {
            pictureLink = createFileDownloadLinkPort.createDownloadLink(user.getPicturePath(), EXPIRY_DURATION);
        }
        return mapToUserProfile(user, pictureLink);
    }

    private UserProfile mapToUserProfile(User user, String pictureLink) {
        return new UserProfile(user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getBio(),
            user.getLinkedin(),
            pictureLink);
    }
}
