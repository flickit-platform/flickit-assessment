package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDownloadLinkUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitDslDownloadLinkRestController {

    private final GetKitDownloadLinkUseCase useCase;
     private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitId}/dsl-download-link")
    public ResponseEntity<GetKitDslDownloadLinkResponseDto> getDslDownloadLink(@PathVariable("kitId") Long kitId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitDslDownloadLink(toParam(kitId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private GetKitDownloadLinkUseCase.Param toParam(Long kitId, UUID currentUserId) {
        return new GetKitDownloadLinkUseCase.Param(kitId,currentUserId);
    }

    private GetKitDslDownloadLinkResponseDto toResponseDto(String response) {
        return new GetKitDslDownloadLinkResponseDto(response);
    }
}
