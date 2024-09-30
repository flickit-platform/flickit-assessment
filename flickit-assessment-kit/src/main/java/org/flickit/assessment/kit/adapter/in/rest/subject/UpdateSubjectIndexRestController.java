package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase;
import org.flickit.assessment.kit.application.port.in.subject.UpdateSubjectIndexUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateSubjectIndexRestController {

    private final UpdateSubjectIndexUseCase useCase;
    private final UserContext userContext;

    @PutMapping("assessment-kits/{kitVersionId}/subjects/{subjectId}/indexes")
    public ResponseEntity<Void> updateSubjectIndex(@PathVariable("kitVersionId") Long kitVersionId,
                                                   @PathVariable("subjectId") Long subjectId,
                                                   @RequestBody UpdateSubjectIndexRequestDto requestDto) {
        UUID currentUserId = userContext.getUser().id();
        useCase.updateSubjectIndex(toParam(kitVersionId, subjectId, requestDto, currentUserId));
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    private Param toParam(Long kitVersionId,
                          Long subjectId,
                          UpdateSubjectIndexRequestDto requestDto,
                          UUID currentUserId) {
        return new Param(kitVersionId,
            subjectId,
            requestDto.index(),
            currentUserId);
    }
}
