package org.flickit.assessment.kit.adapter.in.rest.user;

import lombok.RequiredArgsConstructor;
import org.flickit.assessment.kit.application.domain.crud.PaginatedResponse;
import org.flickit.assessment.kit.application.port.in.user.GetUserListUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class GetUserListRestController {

    private final GetUserListUseCase useCase;

    @GetMapping("assessment-kits/{kitId}/users")
    public ResponseEntity<PaginatedResponse<GetUserListUseCase.UserListItem>> getUserList(
        @PathVariable("kitId") Long kitId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "50") int size) {
        var userList = useCase.getUserList(toParam(kitId, page, size));
        return new ResponseEntity<>(userList, HttpStatus.OK);
    }

    private GetUserListUseCase.Param toParam(Long kitId, int page, int size) {
        return new GetUserListUseCase.Param(kitId, page, size);
    }


}
