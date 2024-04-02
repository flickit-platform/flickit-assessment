package org.flickit.assessment.kit.adapter.in.rest.kitdsl;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitDetailUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitDetailRestController {

    private final GetKitDetailUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/assessment-kits/{kitVersionId}/details")
    public ResponseEntity<GetKitDetailResponseDto> getDslDownloadLink(@PathVariable("kitVersionId") Long kitVersionId) {
        UUID currentUserId = userContext.getUser().id();
        var response = useCase.getKitDetail(toParam(kitVersionId, currentUserId));
        return new ResponseEntity<>(toResponseDto(response), HttpStatus.OK);
    }

    private GetKitDetailUseCase.Param toParam(Long kitVersionId, UUID currentUserId) {
        return new GetKitDetailUseCase.Param(kitVersionId, currentUserId);
    }

    private GetKitDetailResponseDto toResponseDto(GetKitDetailUseCase.Result result) {
        return new GetKitDetailResponseDto(result.maturityLevels(), result.subjects(), result.questionnaires());
    }
}
