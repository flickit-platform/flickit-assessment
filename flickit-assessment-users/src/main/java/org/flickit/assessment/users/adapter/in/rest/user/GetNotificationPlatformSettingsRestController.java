package org.flickit.assessment.users.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.user.GetNotificationPlatformSettingsUseCase;
import org.flickit.assessment.users.application.port.in.user.GetNotificationPlatformSettingsUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class GetNotificationPlatformSettingsRestController {

    private final GetNotificationPlatformSettingsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/users/notification-platform-settings")
    public ResponseEntity<Map<String, String>> getNotificationPlatformSettings() {
        var currentUserId = userContext.getUser().id();
        var responseDto = useCase.getNotificationPlatformSettings(new Param(currentUserId)).settings();
        return new ResponseEntity<>(responseDto, HttpStatus.OK);
    }
}
