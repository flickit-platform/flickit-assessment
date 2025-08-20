package org.flickit.assessment.core.adapter.in.rest.space;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.core.adapter.in.rest.space.GetAssessmentMoveTargetsResponseDto.SpaceListItemDto;
import org.flickit.assessment.core.application.port.in.space.GetAssessmentMoveTargetsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetAssessmentMoveTargetsRestController {

    private final GetAssessmentMoveTargetsUseCase getAssessmentMoveTargetsUseCase;
    private final UserContext userContext;

    @GetMapping("/assessments/{assessmentId}/move-targets")
    public ResponseEntity<GetAssessmentMoveTargetsResponseDto> getAssessmentMoveTargets(@PathVariable UUID assessmentId) {
        UUID currentUserId = userContext.getUser().id();
		var result = getAssessmentMoveTargetsUseCase.getTargetSpaces(toParam(assessmentId, currentUserId));
        return new ResponseEntity<>(toResponseDto(result), HttpStatus.OK);
    }

    private GetAssessmentMoveTargetsResponseDto toResponseDto(GetAssessmentMoveTargetsUseCase.Result result) {
        var itemDtos = result.items().stream()
            .map(r -> new SpaceListItemDto(r.id(),
                r.title(),
                SpaceListItemDto.TypeDto.of(r.type()),
                r.selected(),
                r.isDefault()))
            .toList();

        return new GetAssessmentMoveTargetsResponseDto(itemDtos);
    }

    private static GetAssessmentMoveTargetsUseCase.Param toParam(UUID assessmentId, UUID currentUserId) {
        return new GetAssessmentMoveTargetsUseCase.Param(assessmentId, currentUserId);
    }
}
