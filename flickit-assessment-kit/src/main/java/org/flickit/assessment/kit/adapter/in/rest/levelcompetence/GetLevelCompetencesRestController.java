package org.flickit.assessment.kit.adapter.in.rest.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase.LevelWithCompetencesListItem;
import org.flickit.assessment.kit.application.port.in.levelcompetence.GetLevelCompetencesUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetLevelCompetencesRestController {

    private final GetLevelCompetencesUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/kit-versions/{kitVersionId}/competence-levels")
    public ResponseEntity<GetLevelCompetencesResponseDto> getLevelCompetences(@PathVariable("kitVersionId") Long kitVersionId) {
        var currentUserId = userContext.getUser().id();
        var response = toResponse(useCase.getLevelCompetences(toParam(kitVersionId, currentUserId)));
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, UUID currentUserId) {
        return new Param(kitVersionId, currentUserId);
    }

    private GetLevelCompetencesResponseDto toResponse(List<LevelWithCompetencesListItem> levelCompetences) {
        return new GetLevelCompetencesResponseDto(levelCompetences);
    }
}
