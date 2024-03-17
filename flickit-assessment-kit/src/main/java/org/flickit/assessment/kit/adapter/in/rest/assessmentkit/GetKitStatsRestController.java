package org.flickit.assessment.kit.adapter.in.rest.assessmentkit;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.port.in.assessmentkit.GetKitStatsUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetKitStatsRestController {

    private final GetKitStatsUseCase useCase;

    @GetMapping("assessment-kits/{assessment_kit_id}/stats")
    public ResponseEntity<GetKitStatsResponseDto> getKitStats(@PathVariable("assessment_kit_id") Long assessmentKitId) {
        GetKitStatsUseCase.Result kitStatsResult = useCase.getKitStats(toParam(assessmentKitId));
        return new ResponseEntity<>(toResponse(kitStatsResult), HttpStatus.OK);
    }

    private GetKitStatsUseCase.Param toParam(Long assessmentKitId) {
        return new GetKitStatsUseCase.Param(assessmentKitId);
    }

    private GetKitStatsResponseDto toResponse(GetKitStatsUseCase.Result kitStatsResult) {
        return new GetKitStatsResponseDto(
            kitStatsResult.creationTime(),
            kitStatsResult.lastUpdateTime(),
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
