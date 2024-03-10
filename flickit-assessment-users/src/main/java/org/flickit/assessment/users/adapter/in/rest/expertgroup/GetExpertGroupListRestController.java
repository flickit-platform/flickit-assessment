package org.flickit.assessment.users.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.common.config.jwt.UserContext;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase;
import org.flickit.assessment.users.application.port.in.expertgroup.GetExpertGroupListUseCase.ExpertGroupListItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@Validated
@RestController
@RequiredArgsConstructor
public class GetExpertGroupListRestController {

    private final GetExpertGroupListUseCase useCase;
    private final UserContext userContext;

    @GetMapping("/expert-groups")
    public ResponseEntity<PaginatedResponse<ExpertGroupListItem>> getExpertGroupList(
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {

        var currentUserId = userContext.getUser().id();
        var expertGroupList = useCase.getExpertGroupList(toParam(size, page, currentUserId));
        return new ResponseEntity<>(expertGroupList, HttpStatus.OK);
    }

    private GetExpertGroupListUseCase.Param toParam(int size, int page, UUID currentUserId) {
        return new GetExpertGroupListUseCase.Param(size, page, currentUserId);
    }
}
