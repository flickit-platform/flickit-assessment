package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectRestController {

    private final UpdateSubjectUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/kit-versions/{kitVersionId}/subjects/{subjectId}")
    public ResponseEntity<Void> updateSubject(@PathVariable("kitVersionId") Long kitVersionId,
                                              @PathVariable("subjectId") Long subjectId,
                                              @RequestBody UpdateSubjectRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubject(toParam(kitVersionId, subjectId, currentUserId, requestDto));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateSubjectUseCase.Param toParam(Long kitVersionId,
                                               Long subjectId,
                                               UUID currentUserId,
                                               UpdateSubjectRequestDto updateSubjectRequestDto) {

        return new UpdateSubjectUseCase.Param(kitVersionId,
            subjectId,
            updateSubjectRequestDto.index(),
            updateSubjectRequestDto.title(),
            updateSubjectRequestDto.description(),
            updateSubjectRequestDto.weight(),
            currentUserId);
    }
}
