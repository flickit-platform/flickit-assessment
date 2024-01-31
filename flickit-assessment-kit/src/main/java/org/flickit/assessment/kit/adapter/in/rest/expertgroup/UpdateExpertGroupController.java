package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.kit.application.port.in.expertgroup.UpdateExpertGroupUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class UpdateExpertGroupController {

    private final UpdateExpertGroupUseCase useCase;
    private final UserContext userContext;

    @PutMapping("/expert-groups/{id}")
    public ResponseEntity<Void> updateExpertGroupList(@PathVariable long id,
                                                      @ModelAttribute UpdateExpertGroupRequestDto request) {
        var currentUserId = userContext.getUser().id();
        useCase.updateExpertGroup(toParam(id, request, currentUserId));
        return new ResponseEntity<>(HttpStatus.OK);
    }

    private UpdateExpertGroupUseCase.Param toParam(long id,
                                                   UpdateExpertGroupRequestDto request,
                                                   UUID currentUserId) {
        return new UpdateExpertGroupUseCase.Param(
            id,
            request.title(),
            request.bio(),
            request.about(),
            request.picture(),
            request.website(),
            currentUserId
        );
    }
}
