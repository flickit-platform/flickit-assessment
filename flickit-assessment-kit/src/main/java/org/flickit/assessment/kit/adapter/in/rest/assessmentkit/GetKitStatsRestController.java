package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetKitStatsRestController {

    private final GetKitStatsUseCase useCase;
    private final UserContext userContext;

    @GetMapping("assessment-kits/{kitId}/stats")
    public ResponseEntity<GetKitStatsResponseDto> getKitStats(@PathVariable("kitId") Long kitId) {
        UUID currentUserId = userContext.getUser().id();
        GetKitStatsUseCase.Result kitStatsResult = useCase.getKitStats(toParam(kitId, currentUserId));
        return new ResponseEntity<>(toResponse(kitStatsResult), HttpStatus.OK);
    }

    private GetKitStatsUseCase.Param toParam(Long kitId, UUID currentUserId) {
        return new GetKitStatsUseCase.Param(kitId, currentUserId);
    }

    private GetKitStatsResponseDto toResponse(GetKitStatsUseCase.Result kitStatsResult) {
        return new GetKitStatsResponseDto(
            kitStatsResult.creationTime(),
            kitStatsResult.lastModificationTime(),
            kitStatsResult.questionnairesCount(),
            kitStatsResult.attributesCount(),
            kitStatsResult.questionsCount(),
            kitStatsResult.maturityLevelsCount(),
            kitStatsResult.likes(),
            kitStatsResult.assessmentCounts(),
            kitStatsResult.subjects(),
            kitStatsResult.expertGroup()
        );
    }
}
