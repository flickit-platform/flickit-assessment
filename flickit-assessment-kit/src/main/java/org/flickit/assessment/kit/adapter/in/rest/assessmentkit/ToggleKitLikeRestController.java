package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.ToggleKitLikeUseCase;
import org.flickit.assessment.kit.application.service.assessmentkit.notification.ToggleKitLikeNotificationCmd;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ToggleKitLikeRestController {

    private final ToggleKitLikeUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/assessment-kits/{kitId}/likes")
    public ResponseEntity<ToggleKitLikeResponseDto> toggleKitLike(@PathVariable("kitId") Long kitId) {
        var currentUserId = userContext.getUser().id();
        var result = useCase.toggleKitLike(toParam(kitId, currentUserId));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private ToggleKitLikeUseCase.Param toParam(Long kitId, UUID currentUserId) {
        return new ToggleKitLikeUseCase.Param(kitId, currentUserId);
    }

    private ToggleKitLikeResponseDto toResponse(ToggleKitLikeUseCase.Result result) {
        var notificationCmd = (ToggleKitLikeNotificationCmd) result.notificationCmd();
        return new ToggleKitLikeResponseDto(notificationCmd.likesCount(), notificationCmd.liked());
    }
}
