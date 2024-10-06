package org.flickit.assessment.kit.adapter.in.rest.subject;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase;
import org.flickit.assessment.kit.application.port.in.subject.GetSubjectListUseCase.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class GetSubjectKitRestController {

    private final GetSubjectListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("kit-versions/{kitVersionId}/subjects")
    public ResponseEntity<PaginatedResponse<SubjectListItem>> getSubjectList(@PathVariable("kitVersionId") Long kitVersionId,
                                                                             @RequestParam(defaultValue = "0") int page,
                                                                             @RequestParam(defaultValue = "20") int size) {
        var currentUserId = userContext.getUser().id();
        return new ResponseEntity<>(useCase.getSubjectList(toParam(kitVersionId, page, size, currentUserId)), HttpStatus.OK);
    }

    private Param toParam(Long kitVersionId, int page, int size, UUID currentUserId) {
        return new Param(kitVersionId, page, size, currentUserId);
    }
}
