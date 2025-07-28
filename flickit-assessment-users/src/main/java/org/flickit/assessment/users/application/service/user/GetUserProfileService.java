package org.flickit.assessment.users.application.service.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.users.application.domain.User;
import org.flickit.assessment.users.application.port.in.user.GetUserProfileUseCase;
import org.flickit.assessment.users.application.port.out.minio.CreateFileDownloadLinkPort;
import org.flickit.assessment.users.application.port.out.user.LoadUserPort;
import org.flickit.assessment.users.application.port.out.usersurvey.LoadUserSurveyPort;
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
    private final LoadUserSurveyPort loadUserSurveyPort;

    @Override
    public UserProfile getUserProfile(Param param) {
        User user = loadUserPort.loadUser(param.getCurrentUserId());

        String pictureLink = null;
        if (user.getPicturePath() != null && !user.getPicturePath().trim().isBlank())
            pictureLink = createFileDownloadLinkPort.createDownloadLink(user.getPicturePath(), EXPIRY_DURATION);

        var showSurvey = loadUserSurveyPort.loadByUserId(param.getCurrentUserId())
            .map(s -> !(s.isCompleted() || s.isDontShowAgain()))
            .orElse(true);

        return mapToUserProfile(user,
            pictureLink,
            showSurvey);
    }

    private UserProfile mapToUserProfile(User user, String pictureLink, boolean showSurvey) {
        return new UserProfile(user.getId(),
            user.getEmail(),
            user.getDisplayName(),
            user.getBio(),
            user.getLinkedin(),
            pictureLink,
            showSurvey);
    }
}
