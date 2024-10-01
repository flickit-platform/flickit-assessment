package org.flickit.assessment.kit.adapter.in.rest.levelcompetence;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.levelcompetence.CreateLevelCompetenceUseCase;
import org.flickit.assessment.kit.application.port.in.levelcompetence.CreateLevelCompetenceUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class CreateLevelCompetenceRestController {

    private final CreateLevelCompetenceUseCase useCase;
    private final UserContext userContext;

    @PostMapping("/kit-versions/{kitVersionId}/level-competences")
    public ResponseEntity<Void> createLevelCompetence(@PathVariable("kitVersionId") Long kitVersionId,
                                                      @RequestBody CreateLevelCompetenceRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.createLevelCompetence(toParam(kitVersionId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId, CreateLevelCompetenceRequestDto requestDto, UUID currentUserId) {
        return new Param(kitVersionId,
            requestDto.affectedLevelId(),
            requestDto.effectiveLevelId(),
            requestDto.value(),
            currentUserId);
    }
}
