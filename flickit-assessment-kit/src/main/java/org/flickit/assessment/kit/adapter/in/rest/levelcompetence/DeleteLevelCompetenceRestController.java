package org.flickit.assessment.kit.adapter.in.rest.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.levelcompetence.DeleteLevelCompetenceUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteLevelCompetenceRestController {

    private final DeleteLevelCompetenceUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("/kit-versions/{kitVersionId}/level-competences/{levelCompetenceId}")
    public ResponseEntity<Void> deleteLevelCompetence(@PathVariable("kitVersionId") Long kitVersionId,
                                                      @PathVariable("levelCompetenceId") Long levelCompetenceId) {
        UUID currentUserId = userContext.getUser().id();
        useCase.deleteLevelCompetence(toParam(kitVersionId, levelCompetenceId, currentUserId));
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private DeleteLevelCompetenceUseCase.Param toParam(Long kitVersionId, Long levelCompetenceId, UUID currentUserId) {
        return new DeleteLevelCompetenceUseCase.Param(kitVersionId, levelCompetenceId, currentUserId);
    }
}
