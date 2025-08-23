package org.flickit.assessment.kit.adapter.in.rest.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.levelcompetence.UpdateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.in.levelcompetence.UpdateLevelCompetenceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateLevelCompetenceRestController {

    private final UpdateLevelCompetenceUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/level-competences/{levelCompetenceId}")
    public ResponseEntity<Void> updateLevelCompetence(@PathVariable("kitVersionId") Long kitVersionId,
                                                      @PathVariable("levelCompetenceId") Long levelCompetenceId,
                                                      @RequestBody UpdateLevelCompetenceRequestDto requestDto) {
        var currentUserId = userContext.getUser().id();
        useCase.updateLevelCompetence(toParam(levelCompetenceId, kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private Param toParam(Long levelCompetenceId, Long kitVersionId, UpdateLevelCompetenceRequestDto requestDto, UUID currentUserId) {
        return new Param(levelCompetenceId, kitVersionId, requestDto.value(), currentUserId);
    }
}
