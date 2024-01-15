package org.flickit.assessment.kit.adapter.in.rest.expertgroup;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.common.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.expertgroup.GetExpertGroupMembersUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
public class GetExpertGroupMembersRestController {

    private final GetExpertGroupMembersUseCase useCase;

    @GetMapping("/expert-groups/{id}/users")
    public ResponseEntity<PaginatedResponse<GetExpertGroupMembersUseCase.Member>> getExpertGroupList(
        @PathVariable("id") long id,
        @RequestParam(defaultValue = "10") int size,
        @RequestParam(defaultValue = "0") int page) {

        var memberPaginatedResponse = useCase.getExpertGroupMembers(toParam(size, page, id));
        return new ResponseEntity<>(memberPaginatedResponse, HttpStatus.OK);
    }

    private GetExpertGroupMembersUseCase.Param toParam(int size, int page, long expertGroupId) {
        return new GetExpertGroupMembersUseCase.Param(size, page, expertGroupId);
    }
}
