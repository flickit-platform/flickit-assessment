package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.DeleteSubjectUseCase;
import org.flickit.assessment.kit.application.port.in.subject.DeleteSubjectUseCase.Param;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class DeleteSubjectRestController {

    private final DeleteSubjectUseCase useCase;
    private final UserContext userContext;

    @DeleteMapping("kit-versions/{kitVersionId}/subjects/{subjectId}")
    public ResponseEntity<Void> deleteSubject(@PathVariable("kitVersionId") Long kitVersionId,
                                              @PathVariable("subjectId") Long subjectId) {
        var currentUserId = userContext.getUser().id();
        useCase.deleteSubject(toParam(kitVersionId, subjectId, currentUserId));

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    private Param toParam(Long kitVersionId, Long subjectId, UUID currentUserId) {
        return new Param(subjectId, kitVersionId, currentUserId);
    }
}
