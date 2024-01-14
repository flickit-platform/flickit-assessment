package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.domain.ExpertGroup;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class GetExpertGroupRestController {

    private final GetExpertGroupUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/expert-groups/{id}")
    public ResponseEntity<ExpertGroup> getExpertGroupList(
        @PathVariable("id") long id) {
        var currentUserId = userContext.getUser().id();
        var expertGroup = useCase.getExpertGroup(toParam(id, currentUserId));
        return new ResponseEntity<>(expertGroup, HttpStatus.OK);
    }

    private GetExpertGroupUseCase.Param toParam(long id, UUID currentUserId) {
        return new GetExpertGroupUseCase.Param(id, currentUserId);
    }
}
