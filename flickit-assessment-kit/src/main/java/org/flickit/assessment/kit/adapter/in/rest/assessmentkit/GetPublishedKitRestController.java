package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase.Param;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetPublishedKitUseCase.Result;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetPublishedKitRestController {

    private final GetPublishedKitUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}")
    public ResponseEntity<GetPublishedKitResponseDto> getPublishedKit(@PathVariable("kitId") Long kitId) {
        UUID currentUserId = userContext.getUser().id();
        Result result = useCase.getPublishedKit(toParam(kitId, currentUserId));
        return new ResponseEntity<>(toResponse(result), HttpStatus.OK);
    }

    private Param toParam(Long kitId, UUID currentUserId) {
        return new Param(kitId, currentUserId);
    }

    private GetPublishedKitResponseDto toResponse(Result result) {
        return new GetPublishedKitResponseDto(
            result.id(),
            result.title(),
            result.summary(),
            result.about(),
            result.published(),
            result.isPrivate(),
            result.creationTime(),
            result.lastModificationTime(),
            result.like(),
            result.assessmentsCount(),
            result.subjectsCount(),
            result.questionnairesCount(),
            result.expertGroupId(),
            result.subjects(),
            result.questionnaires(),
            result.maturityLevels(),
            result.tags());
    }
}
